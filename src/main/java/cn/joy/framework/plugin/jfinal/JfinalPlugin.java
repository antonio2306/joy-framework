package cn.joy.framework.plugin.jfinal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.exception.MainError;
import cn.joy.framework.exception.MainErrorType;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.plugin.ITransactionPlugin;
import cn.joy.framework.rule.RuleResult;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

public class JfinalPlugin implements ITransactionPlugin{
	private static Logger logger = Logger.getLogger(JfinalPlugin.class);
	
	public void start() {
	}

	public void stop() {
	}

	public RuleResult doTransaction(final JoyCallback callback, final int transactionWay) throws Exception {
		if(transactionWay==0)
			return callback.run();
		
		RuleResult ruleResult = null;
		final List<RuleResult> resultWrap = new ArrayList<RuleResult>();
		try {
			try {
				Db.tx(new IAtom() {
					public boolean run() throws SQLException {
						try {
							RuleResult txResult = callback.run();
							if(logger.isDebugEnabled())
								logger.debug("doTransaction, txResult="+txResult.toJSON());
							resultWrap.add(txResult);
							return txResult.isSuccess();
						} catch (Exception e) {
							logger.error("", e);
							return false;
						}
					}
				});
			} catch (RuntimeException e) {
				logger.warn(e.getMessage());
			}
			
			if(resultWrap.size()==0)
				return ruleResult.fail(MainError.create(MainErrorType.MISSING_RESULT));
			
			ruleResult = resultWrap.get(0);
			if(logger.isDebugEnabled())
				logger.debug("doTransaction, result="+ruleResult.isSuccess());
			if(ruleResult.isSuccess()){
			}else
				throw new RuleException(ruleResult);
		} catch (Exception e) {
			if(e instanceof RuleException)
				logger.error("RuleException: "+e.getMessage());
			else
				logger.error("", e);
			throw e;
		} finally {
		}
		return ruleResult;
	}
	
}
