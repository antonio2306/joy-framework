package cn.joy.framework.plugin.extention;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.plugin.JoyExtension;
import cn.joy.framework.rule.RuleResult;

public abstract class TransactionExtension implements JoyExtension{
	public abstract RuleResult doTransaction(JoyCallback callback, int transactionWay) throws Exception;
}
