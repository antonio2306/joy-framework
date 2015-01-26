package cn.joy.framework.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.kits.FileKit;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.server.CenterServer;
import cn.joy.framework.server.RouteManager;
/**
 * 规则请求转发处理类
 * @author liyy
 * @date 2015-01-23
 */
public class RuleDispatcher {
	private static Logger logger = Logger.getLogger(RuleDispatcher.class);
	
	private static boolean checkOpenServiceToken(HttpServletRequest request, HttpServletResponse response){
		RuleResult checkResult = JoyManager.getSecurityManager().checkOpenRequest(request);
		if(!checkResult.isSuccess()){
			HttpKit.writeResponse(response, checkResult.toJSON());
			return false;
		}
		return true;
	}
	
	public static String dispatchOpenRule(HttpServletRequest request, HttpServletResponse response) {
		if(!checkOpenServiceToken(request, response))
			return null;
		
		String content = "";
		String ruleURI = request.getParameter("ruleURI");
		if(logger.isDebugEnabled())
			logger.debug("open rule invoke, uri="+ruleURI);
		
		if(StringKit.isNotEmpty(ruleURI)){
			RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
			RuleResult result = null;
			try{
				result = RuleExecutor.create(request).execute(ruleURI, rParam);
			}catch(RuleException e){
				result = e.getFailResult();
			}
			content = result.toJSON();
		}
		HttpKit.writeResponse(response, content);
		return null;
	}
	
	public static String dispatchConfigService(HttpServletRequest request, HttpServletResponse response) {
		if(!checkOpenServiceToken(request, response))
			return null;
		
		String configType = request.getParameter("_t");
		String configKey = request.getParameter("_k");
		if(logger.isDebugEnabled())
			logger.debug("getConfig, type="+configType+", key="+configKey);
		
		String content = "";
		if(JoyManager.getServer() instanceof CenterServer){
			if("route".equals(configType)){
				if("sync_route".equals(configKey)){
					Map<String, Map<String, String>> routeInfo = new HashMap<String, Map<String, String>>();
					routeInfo.put("routes", RouteManager.getRoutes());
					content = JsonKit.object2Json(routeInfo);
				}else
					content = RouteManager.getServerURLByKey(configKey);
			} 
		}
		
		if(logger.isDebugEnabled())
			logger.debug("getConfig, content="+content);
		HttpKit.writeResponse(response, content);
		return null;
	}

	public static String dispatchBusinessRule(HttpServletRequest request, HttpServletResponse response) {
		String content = "";
		String service = request.getParameter("_s");
		String action = request.getParameter("_m");
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, service="+service+", action="+action);
		
		if(StringKit.isEmpty(service) || StringKit.isEmpty(action) ){
			HttpKit.writeResponse(response, "CHECK PARAMETER _s OR _m FAIL");
			return null;
		}
		
		RuleResult checkResult = JoyManager.getSecurityManager().checkBusinessRequest(request);
		if(!checkResult.isSuccess()){
			Map<String, Object> checkResultContent = checkResult.getMapFromContent();
			if(checkResultContent!=null && checkResultContent.containsKey("statusCode"))
				response.setStatus(NumberKit.getInteger(checkResultContent.get("statusCode"), 500));
			
			HttpKit.writeResponse(response, checkResult.getMsg());
			return null;
		}
		
		String ruleURI = service+"."+service+"Controller#"+action;
		if(logger.isDebugEnabled())
			logger.debug("business controller rule invoke, ruleURI="+ruleURI);
		
		RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
		if(rParam==null)
			rParam = RuleParam.create();
		
		String isMergeRequest = RuleKit.getStringParam(request, "imr");
		if(logger.isDebugEnabled())
			logger.debug("isMergeRequest="+isMergeRequest);
		if("y".equals(isMergeRequest)){
			String mergeKey = RuleKit.getStringParam(request, "mk");
			if(logger.isDebugEnabled())
				logger.debug("mergeKey="+mergeKey+", keyValues="+RuleKit.getStringParam(request, mergeKey));
			String[] keyValues = RuleKit.getStringParam(request, mergeKey).split(",");
			Map<String, RuleResult> mergeResult = new HashMap<String, RuleResult>();
			for(String kv:keyValues){
				if(StringKit.isEmpty(kv))
					continue;
				request.setAttribute("MK_"+mergeKey, kv);
				//执行分离，事务分离，合并结果
				RuleResult result = null;
				try{
					result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
				}catch(RuleException e){
					result = e.getFailResult();
				}
				//content = result.toJSON();
				if(logger.isDebugEnabled())
					logger.debug("kv="+kv+", content="+result.toJSON());
				mergeResult.put(kv, result);
				RuleExecutor.clearCurrentExecutor();
			}
			HttpKit.writeResponse(response, JsonKit.object2Json(mergeResult));
		}else{
			RuleResult result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
			String toRender = (String)result.getExtraData("toRender");
			if(StringKit.isNotEmpty(toRender)){
				return toRender;
			}
			
			content = result.toJSON();
			HttpKit.writeResponse(response, content);
			RuleExecutor.clearCurrentExecutor();
		}
		return null;
	}
	
	public static String dispatchDownloadRule(HttpServletRequest request, HttpServletResponse response) {
		String service = request.getParameter("_s");
		String action = request.getParameter("_m");
		if(logger.isDebugEnabled())
			logger.debug("download controller rule invoke, service="+service+", action="+action);
		
		if(StringKit.isEmpty(service) || StringKit.isEmpty(action) ){
			HttpKit.writeResponse(response, "CHECK PARAMETER _s OR _m FAIL");
			return null;
		}
		
		RuleResult checkResult = JoyManager.getSecurityManager().checkBusinessRequest(request);
		if(!checkResult.isSuccess()){
			Map<String, Object> checkResultContent = checkResult.getMapFromContent();
			if(checkResultContent!=null && checkResultContent.containsKey("statusCode"))
				response.setStatus(NumberKit.getInteger(checkResultContent.get("statusCode"), 500));
			
			HttpKit.writeResponse(response, checkResult.getMsg());
			return null;
		}
		
		String ruleURI = service+"."+service+"Controller#"+action;
		if(logger.isDebugEnabled())
			logger.debug("download controller rule invoke, ruleURI="+ruleURI);
		
		RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
		if(rParam==null)
			rParam = RuleParam.create();
		
		RuleResult result = RuleExecutor.create(RuleContext.create(request)).execute(ruleURI, rParam);
		if(result.isSuccess()){
			FileKit.downloadFile(response, result.getMapFromContent());
		}else	
			HttpKit.writeResponse(response, result.toJSON());
		RuleExecutor.clearCurrentExecutor();
		
		return null;
	}
	
	public static String dispatchWebProxy(HttpServletRequest request, HttpServletResponse response) {
		String serverCode = RuleKit.getStringParam(request, "serverCode");
		
		Map<String, Object> datas = new HashMap<String, Object>();
		Map<String, String[]> params = request.getParameterMap();
		for(Entry<String, String[]> entry:params.entrySet()){
			datas.put(entry.getKey(), entry.getValue()[0]);
		}
		
		String serverURL = RouteManager.getServerURLByCompanyCode(serverCode);
		String currentServerURL = RouteManager.getServerURLByTag(RouteManager.getLocalServerTag());
		if(currentServerURL.equals(serverURL)){
			return dispatchBusinessRule(request, response);
		}else{
			String url = JoyManager.getServer().getBusinessRequestUrl(request, RouteManager.getServerURLByCompanyCode(serverCode), "");
			if(logger.isDebugEnabled())
				logger.debug("web proxy url="+url+", datas="+datas);
			HttpKit.writeResponse(response, HttpKit.post(url, datas));
		}
		
		return null;
	}
}
