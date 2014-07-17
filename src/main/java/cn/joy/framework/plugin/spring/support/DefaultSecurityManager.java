package cn.joy.framework.plugin.spring.support;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.rule.RuleResult;
import cn.joy.framework.support.SecurityManager;

public class DefaultSecurityManager implements SecurityManager{
	private Logger logger = Logger.getLogger(DefaultSecurityManager.class);

	public RuleResult checkOpenRequest(HttpServletRequest request) {
		logger.warn("Empty Impl...");
		return RuleResult.create().success();
	}

	public String secureOpenRequestURL(HttpServletRequest request, String requestURL) {
		logger.warn("Empty Impl...");
		return requestURL;
	}

	public RuleResult checkBusinessRequest(HttpServletRequest request) {
		logger.warn("Empty Impl...");
		return RuleResult.create().success();
	}

	public String secureBusinessRequestURL(HttpServletRequest request, String requestURL) {
		logger.warn("Empty Impl...");
		return requestURL;
	}

}
