package cn.joy.framework.plugin.jfinal.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

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

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
/**
 * 通用开放规则调用控制器，负责中心服务器和各应用服务器之间的规则调用，同时负责中心服务器的配置获取服务
 * @author liyy
 * @date 2014-09-10
 */
@Before(OpenRuleInterceptor.class)
public class OpenRuleController extends Controller {
	private Logger logger = Logger.getLogger(OpenRuleController.class);
	
	public void index() {
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		
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
		return;
	}
	
	public void getConfig(){
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		
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
			}else if("get_default_app_file_url".equals(key)){
				content = RouteManager.getDefaultAppFileServerURL();
			}else if("get_app_file_url".equals(key)){
				content = RouteManager.getFileServerURLByTag(tag);
			}else if("sync_route".equals(key)){
				Map<String, Map<String, String>> routeInfo = new HashMap<String, Map<String, String>>();
				routeInfo.put("routes", RouteManager.getRoutes());
				routeInfo.put("routes4File", RouteManager.getRoutes4File());
				content = JsonKit.object2Json(routeInfo);
			}
		}
		
		HttpKit.writeResponse(response, content);
	}

}
