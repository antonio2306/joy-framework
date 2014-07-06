package cn.joy.framework.plugin.spring.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.rule.RuleExecutor;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;
import cn.joy.framework.server.CenterServer;
import cn.joy.framework.server.RouteManager;
/**
 * 通用开放规则调用控制器
 * @author liyy
 * @date 2014-06-11
 */
public class OpenRuleController extends MultiActionController {
	private Logger logger = Logger.getLogger(OpenRuleController.class);
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = "";
		String ruleURI = request.getParameter("ruleURI");
		if(logger.isDebugEnabled())
			logger.debug("open rule invoke, uri="+ruleURI);
		
		if(StringKit.isNotEmpty(ruleURI)){
			RuleParam rParam = (RuleParam)JsonKit.json2Object(request.getParameter("params"), RuleParam.class);
			RuleResult result = RuleExecutor.create(request).execute(ruleURI, rParam);
			content = result.toJSON();
		}
		HttpKit.writeResponse(response, content);
		return null;
	}
	
	public ModelAndView getConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String key = request.getParameter("key");
		String tag = request.getParameter("tag");
		if(logger.isDebugEnabled())
			logger.debug("getConfig, key="+key+", tag="+tag);
		
		String content = "";
		//TODO 各种配置管理需要起来
		if(JoyManager.getServer() instanceof CenterServer){
			if("get_default_app_url".equals(key)){
				content = RouteManager.getDefaultAppServerURL();
			}else if("get_app_url".equals(key)){
				content = RouteManager.getServerURLByTag(tag);
			}
		}
		
		HttpKit.writeResponse(response, content);
		return null;
	}
}