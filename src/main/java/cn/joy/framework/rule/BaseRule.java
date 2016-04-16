package cn.joy.framework.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.annotation.NewTransaction;
import cn.joy.framework.annotation.NoTransaction;
import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.exception.MainError;
import cn.joy.framework.exception.MainErrorType;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.provider.TransactionProvider;
/**
 * 业务规则基类
 * @author liyy
 * @date 2014-05-20
 */
public abstract class BaseRule {
	protected Logger logger = Logger.getLogger(BaseRule.class);
	/**
	 * 规则内部方法转调
	 */
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
			method = this.getClass().getMethod(action, this.getActionMethodParamClass());
		} catch (SecurityException e) {
			logger.error("", e);
			return RuleResult.create().fail(MainError.create(MainErrorType.FORBIDDEN_REQUEST));
		} catch (NoSuchMethodException e) {
			logger.error("", e);
			return RuleResult.create().fail(MainError.create(MainErrorType.INVALID_METHOD));
		}
		
		return doInvokeActionMethod(method, rContext, rParam);
	}
	
	/**
	 * 方法调用包装，可在方法调用前后加入事务控制等
	 */
	protected RuleResult doInvokeActionMethod(final Method method, final RuleContext rContext, final RuleParam rParam) throws Exception{
		final BaseRule rule = this;
		final Object[] mParams = this.getActionMethodParam(rContext, rParam);
		
		/*NoTransaction ntAnnotation = method.getAnnotation(NoTransaction.class);
		if(ntAnnotation!=null){
			return executeRuleMethod(method, rule, mParams);
		}else*/
		TransactionProvider txProvider = TransactionProvider.build();
		if(txProvider!=null){
			return txProvider.doTransaction(new JoyCallback(){
				public RuleResult run(Object... params) throws Exception{
					return executeRuleMethod(method, rule, mParams);
				}
			}, method.getAnnotation(NoTransaction.class)!=null?0:method.getAnnotation(NewTransaction.class)!=null?2:1);
		}else{
			return executeRuleMethod(method, rule, mParams);
		}
		
	}
	
	private RuleResult executeRuleMethod(final Method method, final BaseRule rule, final Object[] mParams) throws Exception{
		RuleResult ruleResult = null;
		try {
			ruleResult = (RuleResult)method.invoke(rule, mParams);
			//if(logger.isDebugEnabled())
			//	logger.debug("doTransactionCallback, method="+method+", result="+ruleResult.toJSON());
		} catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof RuleException)
				throw (RuleException)e.getTargetException();
			else
				throw e;
		}
		if(ruleResult==null)
			ruleResult = RuleResult.create().fail(MainError.create(MainErrorType.MISSING_RESULT));
		if(logger.isDebugEnabled())
			logger.debug("doInvokeActionMethod, method="+method+", result="+ruleResult.isSuccess());
		return ruleResult;
	}

	/**
	 * 根据不同类型的规则，传递不同类型的参数
	 */
	protected Class[] getActionMethodParamClass(){
		if(!this.getClass().getSimpleName().endsWith("ServiceRule"))
			return new Class[]{RuleContext.class, RuleParam.class, HttpServletRequest.class};
		else
			return new Class[]{RuleContext.class, RuleParam.class};
	}
	
	/**
	 * 根据不同类型的规则，传递不同类型的参数
	 */
	protected Object[] getActionMethodParam(RuleContext rContext, RuleParam rParam) {
		if(!this.getClass().getSimpleName().endsWith("ServiceRule"))
			return new Object[]{rContext, rParam, rContext.getRequest()};
		else
			return new Object[]{rContext, rParam};
	}
	
}
