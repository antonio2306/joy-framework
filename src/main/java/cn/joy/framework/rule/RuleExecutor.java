package cn.joy.framework.rule;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.MainError;
import cn.joy.framework.exception.MainErrorType;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.exception.SubError;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.server.RouteManager;
/**
 * 业务规则执行器
 * @author liyy
 * @date 2014-05-20
 */
public class RuleExecutor {
	private static Logger logger = Logger.getLogger(RuleExecutor.class);
	private static ThreadLocal<RuleExecutor> threadRuleExecutor = new ThreadLocal<RuleExecutor>();
	
	private RuleContext rContext;

	private boolean isAsyn = false;
	
	private boolean isRemote = false;
	//同步、本地执行的执行器，绑定到当前线程
	private boolean isThreadBinding = false;
	//是否在调用规则后自动释放资源
	private boolean autoReleaseAfterExecuteOnce = true;

	private RuleExecutor() {

	}
	/**
	 * 建议用于controller调用rule的情形，保证request的可用
	 */
	public static RuleExecutor create(HttpServletRequest request) {
		return create(RuleContext.create(request), null, true);
	}
	/**
	 * 建议用于本地同步调用rule的情形
	 */
	public static RuleExecutor create(RuleContext rContext) {
		return create(rContext, null, true);
	}
	
	public static RuleExecutor create(RuleContext rContext, RuleInvokeConfig config) {
		return create(rContext, config, true);
	}
	
	public static RuleExecutor create(RuleContext rContext, RuleInvokeConfig config, boolean autoReleaseAfterExecuteOnce) {
		RuleExecutor executor = null;
		if(config!=null && config.isAsyn()){
			//本地异步
			executor = new RuleExecutor();
			executor.rContext = rContext;
			executor.isAsyn = true;
			//本地异步，不能提前释放资源，否则会导致RuleContext被提前清空
			executor.autoReleaseAfterExecuteOnce = false;
			rContext.bindExecutor(executor);
		}else{
			//本地同步
			executor = getCurrentExecutor();
			if(logger.isDebugEnabled())
				logger.debug("getCurrentExecutor, executor = "+executor);
			if(executor==null){
				executor = new RuleExecutor();
				executor.rContext = rContext;
				executor.isThreadBinding = true;
				executor.autoReleaseAfterExecuteOnce = autoReleaseAfterExecuteOnce;
				rContext.bindExecutor(executor);
				threadRuleExecutor.set(executor);
			}else{
				if(executor.getRuleContext()==null){
					if(logger.isDebugEnabled())
						logger.debug("getCurrentExecutor, reset executor context="+rContext);
					executor.rContext = rContext;
					rContext.bindExecutor(executor);
				}
			}
		}
		
		if(config!=null)
			rContext.configAs(config);
		
		return executor;
	}
	
	public static RuleExecutor createRemote(RuleContext rContext) {
		return createRemote(rContext, null);
	}
	
	public static RuleExecutor createRemote(RuleContext rContext, RuleInvokeConfig config) {
		RuleExecutor executor = new RuleExecutor();
		executor.rContext = rContext;
		executor.isRemote = true;
		if(config!=null)
			executor.isAsyn = config.isAsyn();
		//远程异步，可能被转为本地异步，也不能提前释放资源，否则会导致RuleContext被提前清空
		if(executor.isAsyn)
			executor.autoReleaseAfterExecuteOnce = false;
		rContext.bindExecutor(executor);
		
		if(config!=null)
			rContext.configAs(config);
		
		return executor;
	}

	public static RuleExecutor getCurrentExecutor() {
		RuleExecutor rExecutor = threadRuleExecutor.get();
		return rExecutor;
	}
	
	public static RuleContext getCurrentContext() {
		RuleExecutor rExecutor = threadRuleExecutor.get();
		if(rExecutor==null)
			return null;
		return rExecutor.getRuleContext();
	}
	
	public static void clearCurrentExecutor() {
		threadRuleExecutor.remove();
	}

	/**
	 * 初始规则调用，一般作为调用的入口
	 */
	public RuleResult execute(String ruleURI, RuleParam rParam){
		return this.executeRule(ruleURI, rParam, false);
	}
	
	/**
	 * 规则中再次调用规则，由上下文调用
	 */
	RuleResult executeInner(String ruleURI, RuleParam rParam) {
		return this.executeRule(ruleURI, rParam, true);
	}

	/**
	 * 根据是否远程、是否异步采用不同的方式调用规则
	 */
	private RuleResult executeRule(String ruleURI, RuleParam rParam, boolean isInnerInvoke) {
		if(logger.isDebugEnabled())
			logger.debug("执行规则【"+ruleURI+"】, params="+rParam);
		if(rParam==null)
			rParam = RuleParam.create();

		RuleResult ruleResult = RuleResult.empty();
		try {
			preExecute();
			
			if(this.isRemote){
				int idx = ruleURI.indexOf("@");
				if(idx!=-1){
					String serverURL = "";
					String serverInfo = ruleURI.substring(0, idx);
					if(serverInfo.startsWith("qc://")){
						serverURL = RouteManager.getServerURLByQyescode(serverInfo.substring(5));
					}else if(serverInfo.startsWith("http://") || serverInfo.startsWith("https://")){
						serverURL = serverInfo;
					}else{
						serverURL = RouteManager.getServerURLByQyescode(serverInfo);
					}
					
					if(StringKit.isNotEmpty(serverURL)){
						if(logger.isDebugEnabled())
							logger.debug("规则【"+ruleURI+"】的服务器地址："+serverURL);
						//如果serverUrl就是当前服务器的URL，则转为本地调用
						String localServerURL = RouteManager.getServerURLByTag(RouteManager.getLocalServerTag());
						if(serverURL.equals(localServerURL)){
							if(logger.isDebugEnabled())
								logger.debug("规则【"+ruleURI+"】转为本地调用");
							ruleResult = executeLocalRule(ruleURI.substring(idx+1), rParam, isInnerInvoke);
						}else{
							ruleResult = executeRemoteRule(serverURL, ruleURI.substring(idx+1), rParam);
						}
					}else
						ruleResult.fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
				}else
					ruleResult.fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
			}else{
				ruleResult = executeLocalRule(ruleURI, rParam, isInnerInvoke);
			}
			
			if(isInnerInvoke&&!ruleResult.isSuccess())
				throw new RuleException(ruleResult);
			
			postExecute(ruleResult);
		} catch (Exception e) {
			if(e instanceof RuleException){
				logger.error("RuleException: "+e.getMessage());
				if(isInnerInvoke)
					throw (RuleException)e;
				else
					ruleResult = ((RuleException)e).getFailResult();
			}else{
				logger.error("", e);
				if(isInnerInvoke)
					throw new RuleException(e);
				else
					ruleResult.fail(MainError.create(MainErrorType.PROGRAM_ERROR));
			}
		} finally {
			if(!isInnerInvoke && autoReleaseAfterExecuteOnce)
				release();
		}
		if(logger.isDebugEnabled())
			logger.debug("执行规则【"+ruleURI+"】, ruleResult="+ruleResult.toJSON());
		return ruleResult;
	}
	
	private RuleResult executeLocalRule(String ruleURI, RuleParam rParam, boolean isInnerInvoke) throws Exception{
		BaseRule rule = JoyManager.getRuleLoader().loadRule(ruleURI);
		if (rule != null){
			if(logger.isDebugEnabled())
				logger.debug("规则【"+ruleURI+"】的类加载器："+rule.getClass().getClassLoader().getClass().getSimpleName());
			if(this.isAsyn)
				return doExecuteAsyn(rule, rParam.putString(RuleParam.KEY_RULE_URI, ruleURI));
			else
				return doExecute(rule, rParam.putString(RuleParam.KEY_RULE_URI, ruleURI), isInnerInvoke);
		}else
			return RuleResult.empty().fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
	}
	
	private RuleResult executeRemoteRule(String serverURL, String remoteRuleURI, RuleParam rParam) throws Exception{
		if(this.isAsyn)
			return doExecuteRemoteAsyn(serverURL, remoteRuleURI, rContext.prepareRemoteContextParam(), rParam);
		else
			return doExecuteRemote(serverURL, remoteRuleURI, rContext.prepareRemoteContextParam(), rParam);
	}
	
	/**
	 * 本地同步调用实现
	 */
	private RuleResult doExecute(BaseRule rule, RuleParam rParam, boolean isInnerInvoke) throws Exception{
		if(logger.isDebugEnabled())
			logger.debug("doExecute, rule="+rule);
		return rule.handleExecuteInternal(rContext, rParam);
	}
	
	/**
	 * 本地异步调用实现
	 */
	private RuleResult doExecuteAsyn(final BaseRule rule, final RuleParam rParam) {
		if(logger.isDebugEnabled())
			logger.debug("doExecuteAsyn, rule="+rule);
		new Thread(new Runnable(){
			public void run() {
				try {
					rule.handleExecuteInternal(rContext, rParam);
				} catch (Exception e) {
					logger.error("", e);
				} finally{
					release();
				}
			}
		}).start();
		return RuleResult.create().success();
	}
	
	/**
	 * 远程同步调用实现
	 */
	private RuleResult doExecuteRemote(String serverURL, String remoteRuleURI, String contextParam, RuleParam rParam) {
		if(logger.isDebugEnabled())
			logger.debug("doExecuteRemote, ruleURI="+remoteRuleURI);
		if(logger.isDebugEnabled())
			logger.debug("远程执行规则【"+remoteRuleURI+"】, url="+serverURL+"/"+contextParam);
		
		RuleResult ruleResult = RuleResult.create();
		String reponseText = post4OpenService(serverURL, contextParam, remoteRuleURI, JsonKit.object2Json(rParam));
		if(logger.isDebugEnabled())
			logger.debug("远程执行规则【"+remoteRuleURI+"】, reponseText="+reponseText);
		if(!StringKit.isEmpty(reponseText)){
			ruleResult = (RuleResult)JsonKit.json2Object(reponseText, RuleResult.class);
			if(ruleResult==null)
				ruleResult = RuleResult.create().fail(reponseText);
		}
		return ruleResult;
	}
	
	/**
	 * 远程异步调用实现
	 */
	private RuleResult doExecuteRemoteAsyn(final String serverURL, final String remoteRuleURI, final String contextParam, final RuleParam rParam) {
		if(logger.isDebugEnabled())
			logger.debug("doExecuteRemoteAsyn, ruleURI="+remoteRuleURI);
		
		new Thread(new Runnable(){
			public void run() {
				if(logger.isDebugEnabled())
					logger.debug("远程执行规则【"+remoteRuleURI+"】, url="+serverURL+"/"+contextParam);
				
				String reponseText = post4OpenService(serverURL, contextParam, remoteRuleURI, JsonKit.object2Json(rParam));
				try {
					if(logger.isDebugEnabled())
						logger.debug("远程执行规则【"+remoteRuleURI+"】, reponseText="+reponseText);
				} catch (Exception e) {
					logger.error("", e);
				} finally{
					release();
				}
			}
		}).start();
		return RuleResult.create().success();
	}
	
	/**
	 * 调用开放规则
	 */
	private String post4OpenService(String url, String contextParam, String serviceKey, String rParamJson){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ruleURI", serviceKey);
		params.put("params", rParamJson);
		
		String serviceURL = url + "/" + JoyManager.getMVCPlugin().getOpenRequestPath(null, "", contextParam, null);
		return HttpKit.post(serviceURL, params);
	}

	private boolean preExecute() {
		return true;
	}

	private void postExecute(RuleResult ruleResult) {
	}

	public void release() {
		try {
			if(logger.isDebugEnabled())
				logger.debug("release... auto="+autoReleaseAfterExecuteOnce);
			if(isThreadBinding){
				threadRuleExecutor.remove();
			}
				
			if(this.rContext!=null)
				rContext.clear();
			this.rContext = null;
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public RuleContext getRuleContext() {
		return rContext;
	}

}
