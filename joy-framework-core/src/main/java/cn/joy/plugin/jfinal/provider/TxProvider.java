package cn.joy.plugin.jfinal.provider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.exception.MainError;
import cn.joy.framework.exception.MainErrorType;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.provider.TransactionProvider;
import cn.joy.framework.rule.RuleResult;

public class TxProvider extends TransactionProvider {
	@Override
	public RuleResult doTransaction(final JoyCallback callback, final int transactionWay) throws Exception {
		if(transactionWay==0)
			return (RuleResult)callback.run();
		
		RuleResult ruleResult = null;
		final List<RuleResult> resultWrap = new ArrayList<RuleResult>();
		try {
			try {
				Db.tx(new IAtom() {
					public boolean run() throws SQLException {
						try {
							RuleResult txResult = (RuleResult)callback.run();
							if(log.isDebugEnabled())
								log.debug("doTransaction, txResult="+txResult.toJSON());
							resultWrap.add(txResult);
							return txResult.isSuccess();
						} catch (Exception e) {
							log.error("", e);
							return false;
						}
					}
				});
			} catch (RuntimeException e) {
				log.warn(e.getMessage());
			}
			
			if(resultWrap.size()==0)
				return RuleResult.create().fail(MainError.create(MainErrorType.MISSING_RESULT));
			
			ruleResult = resultWrap.get(0);
			if(log.isDebugEnabled())
				log.debug("doTransaction, result="+ruleResult.isSuccess());
			if(ruleResult.isSuccess()){
			}else
				throw new RuleException(ruleResult);
		} catch (Exception e) {
			if(e instanceof RuleException)
				log.error("RuleException: "+e.getMessage());
			else
				log.error("", e);
			throw e;
		} finally {
		}
		return ruleResult;
	}

}
