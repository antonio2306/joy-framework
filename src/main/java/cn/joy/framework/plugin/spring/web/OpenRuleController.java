package cn.joy.framework.plugin.spring.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.spring.SpringResource;
import cn.joy.framework.rule.RuleExecutor;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;
import cn.joy.framework.server.CenterServer;
import cn.joy.framework.server.RouteManager;
/**
 * 通用开放规则调用控制器，负责中心服务器和各应用服务器之间的规则调用，同时负责中心服务器的配置获取服务
 * @author liyy
 * @date 2014-06-11
 */
public class OpenRuleController extends MultiActionController {
	private Logger logger = Logger.getLogger(OpenRuleController.class);
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RuleResult checkResult = SpringResource.getSecurityManager().checkOpenRequest(request);
		if(!checkResult.isSuccess()){
			HttpKit.writeResponse(response, checkResult.toJSON());
			return null;
		}
		return super.handleRequestInternal(request, response);
	}
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			String toRender = (String)result.getExtraData("toRender");
			if(StringKit.isNotEmpty(toRender)){
				if(toRender.startsWith("redirect:"))
					return new ModelAndView(new RedirectView(toRender.substring("redirect:".length())));
				else if(toRender.startsWith("jsp:"))
					return new ModelAndView(toRender.substring("jsp:".length()));
			}
			content = result.toJSON();
		}
		HttpKit.writeResponse(response, content);
		return null;
	}
	
	public ModelAndView getConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
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

}
