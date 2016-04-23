package cn.joy.plugin.hibernate3.provider;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.provider.TransactionProvider;
import cn.joy.framework.rule.RuleResult;
import cn.joy.plugin.hibernate3.db.Db;

public class TxProvider extends TransactionProvider {
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
			
			ruleResult = (RuleResult)callback.run();
			if(log.isDebugEnabled())
				log.debug("doTransaction, transactionWay="+transactionWay+", result="+ruleResult.isSuccess());
			if(ruleResult.isSuccess()){
				if(isNew)
					Db.commitAndEndTransaction();
			}else
				throw new RuleException(ruleResult);
		} catch (Exception e) {
			if(e instanceof RuleException)
				log.error("RuleException: "+e.getMessage());
			else
				log.error("", e);
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
