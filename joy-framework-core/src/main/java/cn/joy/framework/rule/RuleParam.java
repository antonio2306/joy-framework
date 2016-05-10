package cn.joy.framework.rule;

import cn.joy.framework.core.JoyBundle;

/**
 * 业务规则执行参数
 * @author liyy
 * @date 2014-05-20
 */
public class RuleParam extends JoyBundle<RuleParam> {
	public static RuleParam create(){
		return new RuleParam();
	}
	
}
