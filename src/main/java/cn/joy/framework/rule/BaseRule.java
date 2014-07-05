package cn.joy.framework.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

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
	
	RuleResult handleExecuteInternal(RuleContext rContext, RuleParam rParam) {
		try {
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
		} catch (Exception e) {
			if(e instanceof RuleException)
				throw (RuleException)e;
			else
				throw new RuleException(MainErrorType.PROGRAM_ERROR);
		}
	}
	
	protected RuleResult doInvokeActionMethod(Method method, RuleContext rContext, RuleParam rParam) throws Exception{
		RuleResult ruleResult = (RuleResult)method.invoke(this, this.getActionMethodParam(rContext, rParam));
		if(ruleResult==null)
			ruleResult = RuleResult.create().fail(MainError.create(MainErrorType.MISSING_RESULT));
		if(logger.isDebugEnabled())
			logger.debug("doInvokeActionMethod, ruleResult="+ruleResult.toJSON());
		return ruleResult;
	}

	protected Class[] getActionMethodParamClass(){
		return new Class[]{RuleContext.class, RuleParam.class};
	}
	
	protected Object[] getActionMethodParam(RuleContext rContext, RuleParam rParam) {
		return new Object[]{rContext, rParam};
	}
	
	protected RuleResult execute(RuleContext rContext, RuleParam rParam) throws Exception{
		return RuleResult.empty().fail("EMPTY　IMPL");
	}
	
	protected RuleResult execute(Object...  params) throws Exception{
		return RuleResult.empty().fail("EMPTY　IMPL");
	}
	
}
