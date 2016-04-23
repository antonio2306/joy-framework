package cn.joy.plugin.spring2.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.rule.RuleDispatcher;
import cn.joy.framework.rule.RuleResult;
/**
 * 通用开放规则调用控制器，负责中心服务器和各应用服务器之间的规则调用，同时负责中心服务器的配置获取服务
 * @author liyy
 * @date 2014-06-11
 */
public class OpenRuleController extends MultiActionController {
	/*protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		RuleResult checkResult = JoyManager.getSecurityManager().checkOpenRequest(request);
		if(!checkResult.isSuccess()){
			HttpKit.writeResponse(response, checkResult.toJSON());
			return null;
		}
		return super.handleRequestInternal(request, response);
	}*/
	
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RuleDispatcher.dispatchOpenRule(request, response);
		return null;
	}
	
}
