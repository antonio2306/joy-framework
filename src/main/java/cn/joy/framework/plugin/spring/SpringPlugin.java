package cn.joy.framework.plugin.spring;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.plugin.ITransactionPlugin;
import cn.joy.framework.plugin.spring.db.Db;
import cn.joy.framework.rule.RuleResult;
/**
 * Spring插件，提供MVC、事务等实现
 * @author liyy
 * @date 2014-07-06
 */
public class SpringPlugin implements ITransactionPlugin{
	private static Logger logger = Logger.getLogger(SpringPlugin.class);
	
	public void start(){
	}
	
	public void stop(){
		
	}

	public RuleResult doTransaction(JoyCallback callback) throws Exception{
		RuleResult ruleResult = null;
		boolean isNew = false;
		try {
			isNew = Db.beginTransaction();
			
			ruleResult = callback.run();
			if(logger.isDebugEnabled())
				logger.debug("doTransaction, result="+ruleResult.isSuccess());
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
			if(isNew)
				Db.endTransaction();
		}
		return ruleResult;
	}

}
