package cn.joy.framework.plugin.jfinal.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.plugin.jfinal.JfinalResource;
import cn.joy.framework.rule.RuleResult;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;

public class OpenRuleInterceptor implements Interceptor{
	public void intercept(ActionInvocation ai) {
		HttpServletRequest request = ai.getController().getRequest();
		HttpServletResponse response = ai.getController().getResponse();
		
		RuleResult checkResult = JfinalResource.getSecurityManager().checkOpenRequest(request);
		if(!checkResult.isSuccess()){
			HttpKit.writeResponse(response, checkResult.toJSON());
		}else
			ai.invoke();
	}
}
