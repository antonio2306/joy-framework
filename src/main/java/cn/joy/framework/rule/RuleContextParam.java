package cn.joy.framework.rule;

import cn.joy.framework.core.JoyBundle;

/**
 * 业务规则执行上下问的构造参数
 * @author liyy
 * @date 2014-06-16
 */
public class RuleContextParam extends JoyBundle<RuleContextParam> {
	public static final RuleContextParam NULL_RULE_CONTEXT_PARAM = RuleContextParam.create();
	
	public static RuleContextParam create(){
		return new RuleContextParam();
	}
	
}
