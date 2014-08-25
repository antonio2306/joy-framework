package cn.joy.framework.plugin;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.plugin.IPlugin;
import cn.joy.framework.rule.RuleResult;
/**
 * 事务插件接口
 * @author liyy
 * @date 2014-07-06
 */
public interface ITransactionPlugin extends IPlugin {
	public RuleResult doTransaction(JoyCallback callback) throws Exception;
}
