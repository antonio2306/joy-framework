package cn.joy.framework.rule;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.exception.SubError;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.kits.FileKit;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.kits.TypeKit;
import cn.joy.framework.server.CenterServer;
import cn.joy.framework.server.RouteManager;

/**
 * 规则请求转发处理类
 * 
 * @author liyy
 * @date 2015-01-23
 */
public class RuleDispatcher{
	private static Logger logger = Logger.getLogger(RuleDispatcher.class);
	public final static String APPID_PARAM_NAME = "_appId";

	/*private static boolean checkOpenServiceToken(HttpServletRequest request, HttpServletResponse response){
		RuleResult checkResult = JoyManager.getSecurityManager().checkOpenRequest(request);
		if(!checkResult.isSuccess()){
			HttpKit.writeResponse(response, checkResult.toJSON());
			return false;
		}
		return true;
	}*/

	public static String dispatchOpenRule(HttpServletRequest request, HttpServletResponse response){
		//if(!checkOpenServiceToken(request, response))
		//	return null;

		String content = "";
		String ruleURI = request.getParameter("ruleURI");
		if(logger.isDebugEnabled())
			logger.debug("open rule invoke, uri=" + ruleURI);

		if(StringKit.isNotEmpty(ruleURI)){
			RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
			
			String serverKey = "";
			if(JoyManager.getServer() instanceof CenterServer)
				serverKey = rParam.getString(RuleKit.SERVER_KEY_PARAM_NAME);
			else
				serverKey = RouteManager.getLocalRouteKey();
			RuleResult result = RuleKit.checkSign(rParam, RouteManager.getServerProp(serverKey, "signKey"));
			if(logger.isDebugEnabled())
				logger.debug("open rule invoke check, result=" + result.isSuccess());
			
			if(result.isSuccess()){
				try{
					String serverProxy = rParam.getString(RuleKit.SERVER_PROXY_PARAM_NAME);
					if(logger.isDebugEnabled())
						logger.debug("serverProxy=" + serverProxy);
					if(JoyManager.getServer() instanceof CenterServer && StringKit.isNotEmpty(serverProxy)){
						rParam.remove(RuleKit.SERVER_PROXY_PARAM_NAME);
						if(logger.isDebugEnabled())
							logger.debug("proxy invoke " + ruleURI);
						result = JoyManager.getRuleExecutor().execute(RuleContext.create(request).uri(serverProxy+"@"+ruleURI), rParam);
					}else
						result = JoyManager.getRuleExecutor().execute(RuleContext.create(request).uri(ruleURI), rParam);
				} catch(RuleException e){
					result = e.getFailResult();
				}
			}
			content = result.toJSON();
		}
		HttpKit.writeResponse(response, content);
		return null;
	}
	
	private static boolean checkAPIRequest(String appId, HttpServletRequest request, HttpServletResponse response){
		RuleResult checkResult = null;
		if(StringKit.isEmpty(appId))
			checkResult = RuleResult.create().fail(SubError.createMain(SubErrorType.ISV_MISSING_PARAMETER, RuleDispatcher.APPID_PARAM_NAME));
		else
			checkResult = JoyManager.getAppAuthManager().checkAPIRequest(request);
		if(!checkResult.isSuccess()){
			HttpKit.writeResponse(response, checkResult.toJSON());
			return false;
		}
		return true;
	}

	public static String dispatchAPIRule(HttpServletRequest request, HttpServletResponse response){
		Long t1 = System.currentTimeMillis();
		String appId = request.getParameter(APPID_PARAM_NAME);
		String clientIP = HttpKit.getClientIP(request);
		String requestId = t1+String.format("%04d", Math.round(Math.random()*10000));
		
		RuleParam rParam = RuleParam.create();
		Enumeration<String> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()){
			String paramName = (String)paramNames.nextElement();
			rParam.put(paramName, request.getParameter(paramName));
		}
		
		String servletPath = request.getServletPath();
		servletPath = servletPath.substring(servletPath.indexOf(JoyManager.getServer().getUrlAPI()) 
				+ JoyManager.getServer().getUrlAPI().length()+1);
		if(logger.isDebugEnabled())
			logger.debug("api rule invoke, uri=" + servletPath);
		
		Logger appLogger = LogKit.getDailyLogger(appId);
		if(appLogger.isDebugEnabled())
			appLogger.debug("调用API[path="+servletPath+",appId="+appId+",reqId="+requestId+",IP="+clientIP+",start="+t1+"]，参数："+rParam);
		if(!checkAPIRequest(appId, request, response))
			return null;
	
		String[] requestInfo = servletPath.split("/");
		String module, service, action;
		if(requestInfo.length == 3){
			module = requestInfo[0];
			service = requestInfo[1];
			action = requestInfo[2];
		}else if(requestInfo.length == 2){
			module = requestInfo[0];
			service = module;
			int idx = service.lastIndexOf(".");
			if(idx!=-1)
				service = service.substring(idx+1);
			action = requestInfo[1];
		}else{
			HttpKit.writeResponse(response, "INVALID_API_URI");
			return null;
		}
		String loginId = request.getParameter("loginId");
		if(StringKit.isEmpty(loginId))
			request.setAttribute(RuleContext.LOGINID_IN_REQUEST, RuleContext.NONE_LOGINID);
		String ruleURI = String.format("%s.%sAPI#%s", module, service, action);
		if(logger.isDebugEnabled())
			logger.debug("api rule invoke, ruleURI=" + ruleURI);

		RuleResult result = null;
		try{
			result = JoyManager.getRuleExecutor().execute(RuleContext.create(request).uri(ruleURI), rParam);
		} catch(RuleException e){
			result = e.getFailResult();
		}
		
		RuleKit.signResult(JoyManager.getAppAuthManager().getAppKey(request), result);
		String content = result.toJSON();
		Long t2 = System.currentTimeMillis();
		if(appLogger.isDebugEnabled())
			appLogger.debug("调用API[rule="+ruleURI+",appId="+appId+",reqId="+requestId+",IP="+clientIP+",end="+t2+",cost="+(t2-t1)/1000.0+"秒]，结果："+content);
		HttpKit.writeResponse(response, content); 
		return null;
	}
	
	public static String dispatchConfigService(HttpServletRequest request, HttpServletResponse response){
		//if(!checkOpenServiceToken(request, response))
		//	return null;

		String configType = request.getParameter("_t");
		String configKey = request.getParameter("_k");
		if(logger.isDebugEnabled())
			logger.debug("getConfig, type=" + configType + ", key=" + configKey);

		String content = "";
		if(JoyManager.getServer() instanceof CenterServer){
			String serverKey = request.getParameter(RuleKit.SERVER_KEY_PARAM_NAME);
			
			boolean isPrivateMode = "private".equals(RouteManager.getServerProp(serverKey, "deployMode"));
			if(isPrivateMode){
				HttpKit.writeResponse(response, "Private");
				return null;
			}
			
			RuleResult result = RuleKit.checkSign(HttpKit.getParameterMap(request), RouteManager.getServerProp(serverKey, "signKey"));
			if(logger.isDebugEnabled())
				logger.debug("config invoke check, result=" + result.isSuccess());
			
			if("route".equals(configType)){
				if("sync_route".equals(configKey)){
					Map<String, Map> routeInfo = new HashMap<String, Map>();
					routeInfo.put("routes", RouteManager.getRoutes());
					routeInfo.put("serverProps", RouteManager.getServerProps());
					content = JsonKit.object2Json(routeInfo);
				} else
					content = RouteManager.getServerURLByKey(configKey);
			}
		}

		if(logger.isDebugEnabled())
			logger.debug("getConfig, content=" + content);
		HttpKit.writeResponse(response, content);
		return null;
	}

	public static String dispatchBusinessRule(HttpServletRequest request, HttpServletResponse response){
		String content = "";
		String service = request.getParameter("_s");
		String action = request.getParameter("_m");
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, service=" + service + ", action=" + action);

		if(StringKit.isEmpty(service) || StringKit.isEmpty(action)){
			HttpKit.writeResponse(response, "CHECK PARAMETER _s OR _m FAIL");
			return null;
		}

		RuleResult checkResult = JoyManager.getSecurityManager().checkBusinessRequest(request);
		if(!checkResult.isSuccess()){
			Map<String, Object> checkResultContent = checkResult.getMapFromContent();
			if(checkResultContent != null && checkResultContent.containsKey("statusCode"))
				response.setStatus(TypeKit.toInt(checkResultContent.get("statusCode"), 500));

			HttpKit.writeResponse(response, checkResult.getMsg());
			return null;
		}

		int idx = service.lastIndexOf(".");
		String ruleURI = service + "." + (idx==-1?service:service.substring(idx+1)) + "Controller#" + action;
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, ruleURI=" + ruleURI);

		RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
		if(rParam == null)
			rParam = RuleParam.create();

		String isMergeRequest = RuleKit.getStringParam(request, "imr");
		if(logger.isDebugEnabled())
			logger.debug("isMergeRequest=" + isMergeRequest);
		if("y".equals(isMergeRequest)){
			String mergeKey = RuleKit.getStringParam(request, "mk");
			if(logger.isDebugEnabled())
				logger.debug("mergeKey=" + mergeKey + ", keyValues=" + RuleKit.getStringParam(request, mergeKey));
			String[] keyValues = RuleKit.getStringParam(request, mergeKey).split(",");
			Map<String, RuleResult> mergeResult = new HashMap<String, RuleResult>();
			for(String kv : keyValues){
				if(StringKit.isEmpty(kv))
					continue;
				request.setAttribute("MK_" + mergeKey, kv);
				// 执行分离，事务分离，合并结果
				RuleResult result = null;
				try{
					result = JoyManager.getRuleExecutor().execute(RuleContext.create(request).uri(ruleURI), rParam);
				} catch(RuleException e){
					result = e.getFailResult();
				}
				// content = result.toJSON();
				if(logger.isDebugEnabled())
					logger.debug("kv=" + kv + ", content=" + result.toJSON());
				mergeResult.put(kv, result);
			}
			HttpKit.writeResponse(response, JsonKit.object2Json(mergeResult));
		} else{
			RuleResult result = JoyManager.getRuleExecutor().execute(RuleContext.create(request).uri(ruleURI), rParam);
			String toRender = (String)result.getExtraData("toRender");
			if(StringKit.isNotEmpty(toRender)){
				return toRender;
			}
			
			if(result.isSuccess()){
				String[] mergeRequestRuleArr = StringKit.getString(request.getParameter("_mrr")).split(",");
				for(String mergeRequestRule:mergeRequestRuleArr){
					int methodFlagIdx = mergeRequestRule.indexOf("#");
					if(methodFlagIdx>0){
						service = mergeRequestRule.substring(0, methodFlagIdx);
						action = mergeRequestRule.substring(methodFlagIdx+1);
						idx = service.lastIndexOf(".");
						ruleURI = service + "." + (idx==-1?service:service.substring(idx+1)) + "Controller#" + action;
						if(logger.isDebugEnabled())
							logger.debug("business controller rule invoke, mergeRequestRuleURI=" + ruleURI);
						
						RuleResult mergeRequestRuleResult = JoyManager.getRuleExecutor().execute(RuleContext.create(request).uri(ruleURI), rParam);
						if(!mergeRequestRuleResult.isSuccess()){
							result = mergeRequestRuleResult;
							break;
						}else{
							result.putExtraData(mergeRequestRule, mergeRequestRuleResult);
						}
					}
				}
			}
			
			content = result.toJSON();
			HttpKit.writeResponse(response, content);
		}
		return null;
	}

	public static String dispatchDownloadRule(HttpServletRequest request, HttpServletResponse response){
		String service = request.getParameter("_s");
		String action = request.getParameter("_m");
		if(logger.isDebugEnabled())
			logger.debug("download controller rule invoke, service=" + service + ", action=" + action);

		if(StringKit.isEmpty(service) || StringKit.isEmpty(action)){
			HttpKit.writeResponse(response, "CHECK PARAMETER _s OR _m FAIL");
			return null;
		}

		RuleResult checkResult = JoyManager.getSecurityManager().checkBusinessRequest(request);
		if(!checkResult.isSuccess()){
			Map<String, Object> checkResultContent = checkResult.getMapFromContent();
			if(checkResultContent != null && checkResultContent.containsKey("statusCode"))
				response.setStatus(TypeKit.toInt(checkResultContent.get("statusCode"), 500));

			HttpKit.writeResponse(response, checkResult.getMsg());
			return null;
		}

		int idx = service.lastIndexOf(".");
		String ruleURI = service + "." + (idx==-1?service:service.substring(idx+1)) + "Controller#" + action;
		if(logger.isDebugEnabled())
			logger.debug("download controller rule invoke, ruleURI=" + ruleURI);

		RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
		if(rParam == null)
			rParam = RuleParam.create();

		RuleResult result = JoyManager.getRuleExecutor().execute(RuleContext.create(request).uri(ruleURI), rParam);
		if(result.isSuccess()){
			FileKit.downloadFile(response, result.getMapFromContent());
		} else
			HttpKit.writeResponse(response, result.toJSON());

		return null;
	}

	public static String dispatchWebProxy(HttpServletRequest request, HttpServletResponse response){
		String serverCode = RuleKit.getStringParam(request, "serverCode");

		Map<String, Object> datas = new HashMap<String, Object>();
		Map<String, String[]> params = request.getParameterMap();
		for(Entry<String, String[]> entry : params.entrySet()){
			datas.put(entry.getKey(), entry.getValue()[0]);
		}
		if(!datas.containsKey(JoyManager.getServer().getSceneKeyParam()))
			datas.put(JoyManager.getServer().getSceneKeyParam(), RuleKit.getStringAttribute(request, JoyManager.getServer().getSessionSceneKeyParam()));
		
		String appServerType = StringKit.getString(request.getParameter("appServerType"), JoyManager.getServer().getAppServerType());

		String serverURL = RouteManager.getServerURL(appServerType, RouteManager.getServerTag(serverCode));
		String currentServerURL = RouteManager.getServerURL(JoyManager.getServer().getAppServerType(),
				RouteManager.getLocalServerTag());
		if(currentServerURL.equals(serverURL)){
			return dispatchBusinessRule(request, response);
		} else{
			String url = JoyManager.getServer().getBusinessRequestUrl(request,
					RouteManager.getServerURLByCompanyCode(serverCode), "");
			if(logger.isDebugEnabled())
				logger.debug("web proxy url=" + url + ", datas=" + datas);
			HttpKit.writeResponse(response, HttpKit.post(url, datas));
		}

		return null;
	}
}
