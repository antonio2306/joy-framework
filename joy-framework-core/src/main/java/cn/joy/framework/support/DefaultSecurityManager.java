package cn.joy.framework.support;

import javax.servlet.http.HttpServletRequest;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.rule.RuleResult;

public class DefaultSecurityManager implements SecurityManager{
	private static Log logger = LogKit.get();

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
