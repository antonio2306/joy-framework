package cn.joy.framework.support;

import javax.servlet.http.HttpServletRequest;

import cn.joy.framework.rule.RuleResult;
/**
 * 安全管理器接口，提供对开放规则、业务规则请求的安全性检查，如对访问Token、参数签名等的验证
 * @author liyy
 * @date 2014-07-06
 */
public interface SecurityManager {
	public RuleResult checkOpenRequest(HttpServletRequest request);
	
	public String secureOpenRequestURL(HttpServletRequest request, String requestURL);
	
	public RuleResult checkBusinessRequest(HttpServletRequest request);
	
	public String secureBusinessRequestURL(HttpServletRequest request, String requestURL);
}
