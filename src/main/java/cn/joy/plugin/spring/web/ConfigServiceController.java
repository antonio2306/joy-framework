package cn.joy.plugin.spring.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.rule.RuleDispatcher;
import cn.joy.framework.rule.RuleResult;

public class ConfigServiceController extends MultiActionController {
	
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
		RuleDispatcher.dispatchConfigService(request, response);
		return null;
	}
	
}
