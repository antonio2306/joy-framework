package cn.joy.plugin.jfinal.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class OpenRuleInterceptor implements Interceptor{
	private Logger logger = Logger.getLogger(OpenRuleInterceptor.class);
	
	public void intercept(Invocation ai) {
		HttpServletRequest request = ai.getController().getRequest();
		HttpServletResponse response = ai.getController().getResponse();
		
		/*RuleResult checkResult = JoyManager.getSecurityManager().checkOpenRequest(request);
		//logger.debug("checkResult="+checkResult.toJSON());
		if(!checkResult.isSuccess()){
			HttpKit.writeResponse(response, checkResult.toJSON());
			ai.getController().renderNull();
		}else*/
			ai.invoke();
	}
}
