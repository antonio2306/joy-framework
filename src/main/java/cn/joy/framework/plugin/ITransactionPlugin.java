package cn.joy.framework.plugin;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.plugin.IPlugin;
import cn.joy.framework.rule.RuleResult;

public interface ITransactionPlugin extends IPlugin {
	public RuleResult doTransaction(JoyCallback callback) throws Exception;
}
