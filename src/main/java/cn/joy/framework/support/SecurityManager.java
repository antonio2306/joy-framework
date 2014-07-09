package cn.joy.framework.support;

import javax.servlet.http.HttpServletRequest;

import cn.joy.framework.rule.RuleResult;

public interface SecurityManager {
	public RuleResult checkOpenRequest(HttpServletRequest request);
	
	public String secureOpenRequestURL(String requestURL);
	
	public RuleResult checkBusinessRequest(HttpServletRequest request);
}
