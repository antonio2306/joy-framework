package cn.joy.framework.support;

import javax.servlet.http.HttpServletRequest;

import cn.joy.framework.rule.RuleResult;

public interface SecurityManager {
	public RuleResult checkRequest(HttpServletRequest request);
}
