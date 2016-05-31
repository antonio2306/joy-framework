package cn.joy.framework.rule;

import java.util.HashMap;
import java.util.Map;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.MainError;
import cn.joy.framework.exception.MainErrorType;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.exception.SubError;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.server.RouteManager;
import cn.joy.framework.task.JoyTask;
import cn.joy.framework.task.TaskExecutor;
/**
 * 业务规则执行器
 * @author liyy
 * @date 2014-05-20
 */
public class RuleExecutor {
	private static Log logger = LogKit.get();
	private static RuleExecutor rExecutor;
	private RuleLoader rLoader;
	
	private RuleExecutor(){
	}
	
	public static RuleExecutor singleton(){
		if(rExecutor==null){
			rExecutor = new RuleExecutor();
			rExecutor.rLoader = new RuleLoader();
		}
		return rExecutor;
	}
	
	public RuleResult execute(RuleContext rContext, RuleParam rParam) {
		return execute(rContext, rParam, false);
	}
	
	public RuleResult executeAsyn(RuleContext rContext, RuleParam rParam) {
		return execute(rContext, rParam, true);
	}
	
	public RuleResult execute(RuleContext rContext, RuleParam rParam, boolean asyn) {
		boolean isDebugLogKey = LogKit.isDebugLogKey(rContext.user());
		if(isDebugLogKey)
			LogKit.setMDC(LogKit.MDC_KEY, rContext.user());
		
		String ruleURI = rContext.uri();
		logger.debug("执行规则【{}】, params={}, asyn={}", ruleURI, rParam, asyn);
		if(rParam==null)
			rParam = RuleParam.create();

		RuleResult ruleResult = RuleResult.create();
		try {
			int idx = ruleURI.indexOf("@");
			if(idx!=-1){
				String serverURL = "";
				String serverKey = "";
				String serverInfo = ruleURI.substring(0, idx);
				if(serverInfo.startsWith("http://") || serverInfo.startsWith("https://")){
					serverURL = serverInfo;
				}else if(serverInfo.indexOf(":")>0){
					serverURL = RouteManager.getServerURLByKey(serverInfo);
					serverKey = serverInfo;
				}else{
					String serverTag = RouteManager.getServerTag(serverInfo);
					if(JoyManager.getServer().getCenterServerTag().equals(serverTag)){
						serverURL = RouteManager.getCenterServerURL();
						serverKey = RouteManager.getCenterRouteKey();
					}else{
						serverURL = RouteManager.getServerURLByTag(serverTag);
						serverKey = RouteManager.getRouteKey("app", serverTag);
					}
				}
				
				//私有部署，没有其它远程服务器的route信息，由网站中转调用
				if(JoyManager.getServer().isPrivateMode() && StringKit.isEmpty(serverURL)){
					serverURL = RouteManager.getCenterServerURL();
					serverKey = RouteManager.getCenterRouteKey();
					rParam.put(RuleKit.SERVER_PROXY_PARAM_NAME, serverInfo);
				}
				
				if(StringKit.isNotEmpty(serverURL)){
					logger.debug("规则【"+ruleURI+"】的服务器地址："+serverURL);
					//如果serverUrl就是当前服务器的URL，则转为本地调用
					String localServerURL = RouteManager.getServerURL(JoyManager.getServer().getAppServerType(), RouteManager.getLocalServerTag());
					
					if(serverURL.equals(localServerURL)){
						logger.debug("规则【"+ruleURI+"】转为本地调用");
						ruleResult = executeLocalRule(rContext.uri(ruleURI.substring(idx+1)), rParam, asyn);
					}else{
						ruleResult = executeRemoteRule(rContext.uri(ruleURI.substring(idx+1)), serverURL, rParam, serverKey, asyn);
					}
				}else
					ruleResult.fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
			}else
				ruleResult = executeLocalRule(rContext, rParam, asyn);
		} catch (Exception e) {
			if(e instanceof RuleException){
				logger.error("RuleException: "+e.getMessage());
				ruleResult = ((RuleException)e).getFailResult();
			}else{
				logger.error("", e);
				ruleResult.fail(MainError.create(MainErrorType.PROGRAM_ERROR));
			}
		} finally{
			if(!asyn)
				rContext.release();
		}

		logger.debug("执行规则【"+ruleURI+"】, ruleResult="+ruleResult.toJSON());
		
		if(isDebugLogKey)
			LogKit.removeMDC(LogKit.MDC_KEY);
		return ruleResult;
	}
	
	private RuleResult executeLocalRule(final RuleContext rContext, final RuleParam rParam, boolean asyn) throws Exception{
		String ruleURI = rContext.uri();
		final BaseRule rule = rLoader.loadRule(ruleURI);
		if (rule != null){
			logger.debug("规则【"+ruleURI+"】的类加载器："+rule.getClass().getClassLoader().getClass().getSimpleName());
			if(asyn){
				TaskExecutor.execute(new JoyTask() {
					public void run() {
						try {
							rule.handleExecuteInternal(rContext, rParam);
						} catch (Exception e) {
							logger.error("", e);
						} finally{
							rContext.release();
						}
					}
				});
				return RuleResult.create().success();
			}else
				return rule.handleExecuteInternal(rContext, rParam);
		}else
			return RuleResult.create().fail(SubError.createMain(SubErrorType.ISP_SERVICE_UNAVAILABLE, ruleURI));
	}
	
	private RuleResult executeRemoteRule(final RuleContext rContext, final String serverURL, 
			final RuleParam rParam, final String serverKey, boolean asyn) throws Exception{
		final String remoteRuleURI = rContext.uri();
		final String contextParam = rContext.prepareRemoteContextParam();
		logger.debug("远程执行规则【{}】, url={}/{}", remoteRuleURI, serverURL, contextParam);
		
		RuleResult ruleResult = RuleResult.create();
		if(asyn){
			TaskExecutor.execute(new JoyTask() {
				public void run() {
					logger.debug("远程异步执行规则【{}】, url={}/{}", remoteRuleURI, serverURL, contextParam);
					
					try {
						String reponseText = post4OpenService(serverURL, contextParam, remoteRuleURI, rParam, serverKey);
						logger.debug("远程执行规则【{}】, reponseText={}", remoteRuleURI, reponseText);
					} catch (Exception e) {
						logger.error("", e);
					} finally{
						rContext.release();
					}
				}
			});
			return ruleResult.success();
		}else{
			String reponseText = post4OpenService(serverURL, contextParam, remoteRuleURI, rParam, serverKey);
			logger.debug("远程执行规则【{}】, reponseText={}", remoteRuleURI, reponseText);
			if(!StringKit.isEmpty(reponseText)){
				ruleResult = (RuleResult)JsonKit.json2Object(reponseText, RuleResult.class);
				if(ruleResult==null)
					ruleResult = RuleResult.create().fail(reponseText);
			}
			return ruleResult;
		}
	}
	
	/**
	 * 调用开放规则
	 */
	private String post4OpenService(String url, String contextParam, String serviceKey, RuleParam rParam, String serverKey){
		logger.debug("post4OpenService, serviceKey="+serviceKey+", serverKey="+serverKey);
		if(StringKit.isNotEmpty(serverKey)){
			//调用center的service，需要提供调用者的serverKey，根据调用者的signKey签名
			if(RouteManager.isCenterRouteKey(serverKey)){
				String localServerKey = RouteManager.getLocalRouteKey();
				String signKey = RouteManager.getServerProp(localServerKey, "signKey");
				
				rParam.put(RuleKit.SERVER_KEY_PARAM_NAME, localServerKey);
				rParam.put(RuleKit.SIGNATURE_PARAM_NAME, RuleKit.getSign(rParam, signKey));
			}else{
				//调用app的service，根据被调用app的signKey签名
				String signKey = RouteManager.getServerProp(serverKey, "signKey");
				rParam.put(RuleKit.SIGNATURE_PARAM_NAME, RuleKit.getSign(rParam, signKey));
			}
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ruleURI", serviceKey);
		params.put("params", JsonKit.object2Json(rParam));
		
		String serviceURL = JoyManager.getServer().getOpenRequestUrl(url, contextParam);
		return HttpKit.post(serviceURL, params);
	}

}
