package cn.joy.framework.provider;

import java.util.Properties;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.rule.RuleResult;

public abstract class TransactionProvider implements JoyProvider{
	protected Log log = LogKit.getLog(TransactionProvider.class);

	public static TransactionProvider build(){
		return (TransactionProvider)JoyManager.provider(TransactionProvider.class);
	}
	
	@Override
	public void init(Properties prop){
		
	}
	
	@Override
	public void release(){
		
	}
	
	public abstract RuleResult doTransaction(JoyCallback callback, int transactionWay) throws Exception;
}
