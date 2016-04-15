package cn.joy.plugin.jfinal.web;

import cn.joy.framework.rule.RuleDispatcher;

public class WebProxyController extends BusinessRuleController {
	public void index(){
		RuleDispatcher.dispatchWebProxy(getRequest(), getResponse());
		renderNull();
	}
}
