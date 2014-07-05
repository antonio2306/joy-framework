package cn.joy.framework.rule;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import cn.joy.framework.exception.MainErrorType;
import cn.joy.framework.exception.RuleException;
/**
 * 支持事务的业务规则基类
 * @author liyy
 * @date 2014-05-20
 */
public class BaseTransactionRule extends BaseRule{
	protected Logger logger = Logger.getLogger(BaseTransactionRule.class);
	
	protected RuleResult doInvokeActionMethod(Method method, RuleContext rContext, RuleParam rParam) throws Exception {
		//TODO  start transaction
		RuleResult ruleResult = null;
		
		try {
			ruleResult = super.doInvokeActionMethod(method, rContext, rParam);

			/*if (newSession){
				//如果执行失败，且不是空结果，则回滚事务
				if(!ruleResult.isSuccess() && !RuleResult.FLAG_EMPTY_RESULT.equals(ruleResult.getMsg()))
					dataset.rollbackAndCloseSession();
				else
					dataset.commitAndCloseSession();
			}*/
		} catch (Exception e) {
			logger.error("执行规则失败:", e);
			/*try {
				if (newSession)
					dataset.rollbackAndCloseSession();
			} catch (Exception e1) {
			}*/
			throw e;
		}
		//TODO  end transaction
		return ruleResult;
	}

}
