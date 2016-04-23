package cn.joy.plugin.spring2.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import cn.joy.framework.rule.RuleDispatcher;

public class WebProxyController extends BusinessRuleController {
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RuleDispatcher.dispatchWebProxy(request, response);
		return null;
	}
}
