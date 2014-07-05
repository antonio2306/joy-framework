package cn.joy.framework.core;

import cn.joy.framework.rule.RuleResult;

public interface JoyCallback {
	public RuleResult run() throws Exception;
}
