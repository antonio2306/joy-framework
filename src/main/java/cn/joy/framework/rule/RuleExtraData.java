package cn.joy.framework.rule;

import cn.joy.framework.core.JoyBundle;

/**
 * 
 * @author liyy
 * @date 2014-05-20
 */
public class RuleExtraData extends JoyBundle<RuleExtraData> {
	public static final RuleExtraData NULL_RULE_EXTRA = RuleExtraData.create();
	
	public static RuleExtraData create(){
		return new RuleExtraData();
	}
	
}
