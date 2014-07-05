package cn.joy.framework.rule;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
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
	
	private boolean isThreadBinding = false;

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

	public RuleResult execute(String ruleURI, RuleParam rParam){
		return this.execute(ruleURI, rParam, false);
	}
	
	RuleResult executeInner(String ruleURI, RuleParam rParam) {
		return this.execute(ruleURI, rParam, true);
	}

	private RuleResult execute(String ruleURI, RuleParam rParam, boolean isInnerInvoke) {
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
						
					}else{
						serverURL = RouteManager.getServerURLByQyescode(serverInfo);
					}
					
					if(StringKit.isNotEmpty(serverURL)){
						//TODO 如果serverUrl就是当前服务器的URL，则转为本地调用。。。
						
						if(logger.isDebugEnabled())
							logger.debug("规则【"+ruleURI+"】的服务器地址："+serverURL);
						String remoteRuleURI = ruleURI.substring(idx+1);
						
						if(this.isAsyn)
							ruleResult = doExecuteRemoteAsyn(serverURL, remoteRuleURI, rContext.prepareRemoteContextParam(), rParam);
						else
							ruleResult = doExecuteRemote(serverURL, remoteRuleURI, rContext.prepareRemoteContextParam(), rParam);
					}else
						ruleResult.fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
				}else
					ruleResult.fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
			}else{
				BaseRule rule = JoyManager.getRuleLoader().loadRule(ruleURI);
				if (rule != null){
					if(logger.isDebugEnabled())
						logger.debug("规则【"+ruleURI+"】的类加载器："+rule.getClass().getClassLoader());
					if(this.isAsyn)
						ruleResult = doExecuteAsyn(rule, rParam.putString(RuleParam.KEY_RULE_URI, ruleURI));
					else
						ruleResult = doExecute(rule, rParam.putString(RuleParam.KEY_RULE_URI, ruleURI), isInnerInvoke);
				}else
					ruleResult.fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
			}
			
			postExecute(ruleResult);
		} catch (RuleException re) {
			logger.error("", re);
			ruleResult.fail(re);
		} catch (Exception e) {
			logger.error("", e);
			ruleResult.fail(new RuleException(e));
		} finally {
			if(!isInnerInvoke && autoReleaseAfterExecuteOnce)
				release();
		}
		if(logger.isDebugEnabled())
			logger.debug("执行规则【"+ruleURI+"】, ruleResult="+ruleResult.toJSON());
		return ruleResult;
	}
	
	private RuleResult doExecute(BaseRule rule, RuleParam rParam, boolean isInnerInvoke){
		if(logger.isDebugEnabled())
			logger.debug("doExecute, rule="+rule);
		return rule.handleExecuteInternal(rContext, rParam);
	}
	
	private RuleResult doExecuteAsyn(final BaseRule rule, final RuleParam rParam) {
		if(logger.isDebugEnabled())
			logger.debug("doExecuteAsyn, rule="+rule);
		new Thread(new Runnable(){
			public void run() {
				rule.handleExecuteInternal(rContext, rParam);
			}
		}).start();
		return RuleResult.create().success();
	}
	
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
				}
			}
		}).start();
		return RuleResult.create().success();
	}
	
	private String post4OpenService(String url, String contextParam, String serviceKey, String rParamJson){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ruleURI", serviceKey);
		params.put("params", rParamJson);
		
		String serviceURL = url + "/" + JoyManager.getMVCPlugin().getOpenRequestPath("", contextParam, null);
		return HttpKit.post(serviceURL, params);
	}

	private boolean preExecute() {
		return true;
	}

	private void postExecute(RuleResult ruleResult) {
	}

	public void release() {
		try {
			if(isThreadBinding){
				threadRuleExecutor.remove();
				//if(this.rContext!=null)
				//	this.rContext.getDataset().getThreadLocal().remove();
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
