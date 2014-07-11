package cn.joy.framework.core;

import cn.joy.framework.rule.RuleResult;
/**
 * 通用回调接口
 * @author liyy
 * @date 2014-07-05
 */
public interface JoyCallback {
	public RuleResult run(Object... params) throws Exception;
}
