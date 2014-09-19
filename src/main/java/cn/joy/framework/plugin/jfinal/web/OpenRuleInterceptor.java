package cn.joy.framework.plugin.jfinal.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.plugin.jfinal.JfinalResource;
import cn.joy.framework.rule.RuleResult;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;

public class OpenRuleInterceptor implements Interceptor{
	private Logger logger = Logger.getLogger(OpenRuleInterceptor.class);
	
	public void intercept(ActionInvocation ai) {
		HttpServletRequest request = ai.getController().getRequest();
		HttpServletResponse response = ai.getController().getResponse();
		
		RuleResult checkResult = JfinalResource.getSecurityManager().checkOpenRequest(request);
		//logger.debug("checkResult="+checkResult.toJSON());
		if(!checkResult.isSuccess()){
			HttpKit.writeResponse(response, checkResult.toJSON());
			ai.getController().renderNull();
		}else
			ai.invoke();
	}
}
