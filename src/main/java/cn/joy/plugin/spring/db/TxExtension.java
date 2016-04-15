package cn.joy.plugin.spring.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.plugin.extention.TransactionExtension;
import cn.joy.framework.rule.RuleResult;

public class TxExtension extends TransactionExtension {
	private Logger logger = LoggerFactory.getLogger(TxExtension.class);

	@Override
	public RuleResult doTransaction(JoyCallback callback, int transactionWay) throws Exception{
		RuleResult ruleResult = null;
		boolean isNew = false;
		try {
			if(transactionWay==0)
				isNew = Db.beginEmptyTransaction();
			else if(transactionWay==1)
				isNew = Db.beginTransaction();
			else if(transactionWay==2)
				isNew = Db.beginNewTransaction();
			
			ruleResult = callback.run();
			if(logger.isDebugEnabled())
				logger.debug("doTransaction, transactionWay="+transactionWay+", result="+ruleResult.isSuccess());
			if(ruleResult.isSuccess()){
				if(isNew)
					Db.commitAndEndTransaction();
			}else
				throw new RuleException(ruleResult);
		} catch (Exception e) {
			if(e instanceof RuleException)
				logger.error("RuleException: "+e.getMessage());
			else
				logger.error("", e);
			if(isNew)
				Db.rollbackAndEndTransaction();
			throw e;
		} finally {
			//if(isNew)	//session栈，不能多次关闭了
			//	Db.endTransaction();
		}
		return ruleResult;
	}

}
