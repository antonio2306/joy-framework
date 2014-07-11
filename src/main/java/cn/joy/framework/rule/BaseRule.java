package cn.joy.framework.rule;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.MainError;
import cn.joy.framework.exception.MainErrorType;
import cn.joy.framework.exception.RuleException;
/**
 * 业务规则基类
 * @author liyy
 * @date 2014-05-20
 */
public abstract class BaseRule {
	protected Logger logger = Logger.getLogger(BaseRule.class);
	
	RuleResult handleExecuteInternal(RuleContext rContext, RuleParam rParam) throws Exception{
		String ruleURI = rParam.getString(RuleParam.KEY_RULE_URI);
		int idx = ruleURI.indexOf("#");
		String action = "execute";
		if(idx>0){
			action = ruleURI.substring(idx+1);
			if(logger.isDebugEnabled())
				logger.debug("execute action="+action);
		}

		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod(action, this.getActionMethodParamClass());
		} catch (SecurityException e) {
			logger.error("", e);
			return RuleResult.create().fail(MainError.create(MainErrorType.FORBIDDEN_REQUEST));
		} catch (NoSuchMethodException e) {
			logger.error("", e);
			return RuleResult.create().fail(MainError.create(MainErrorType.INVALID_METHOD));
		}
		
		return doInvokeActionMethod(method, rContext, rParam);
	}
	
	protected RuleResult doInvokeActionMethod(final Method method, final RuleContext rContext, final RuleParam rParam) throws Exception{
		final BaseRule rule = this;
		final Object[] mParams = this.getActionMethodParam(rContext, rParam);
		
		return JoyManager.getTransactionPlugin().doTransaction(new JoyCallback(){
			public RuleResult run(Object... params) throws Exception{
				RuleResult ruleResult = (RuleResult)method.invoke(rule, mParams);
				if(ruleResult==null)
					ruleResult = RuleResult.create().fail(MainError.create(MainErrorType.MISSING_RESULT));
				if(logger.isDebugEnabled())
					logger.debug("doInvokeActionMethod, ruleResult="+ruleResult.toJSON());
				return ruleResult;
			}
		});
	}

	protected Class[] getActionMethodParamClass(){
		if(this.getClass().getSimpleName().endsWith("ControllerRule"))
			return new Class[]{RuleContext.class, RuleParam.class, HttpServletRequest.class};
		else
			return new Class[]{RuleContext.class, RuleParam.class};
	}
	
	protected Object[] getActionMethodParam(RuleContext rContext, RuleParam rParam) {
		if(this.getClass().getSimpleName().endsWith("ControllerRule"))
			return new Object[]{rContext, rParam, rContext.getRequest()};
		else
			return new Object[]{rContext, rParam};
	}
	
}
