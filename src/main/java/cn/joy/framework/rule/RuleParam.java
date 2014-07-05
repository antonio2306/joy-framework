package cn.joy.framework.rule;

import cn.joy.framework.core.JoyBundle;

/**
 * 业务规则执行参数
 * @author liyy
 * @date 2014-05-20
 */
public class RuleParam extends JoyBundle<RuleParam> {
	public static final String KEY_RULE_URI = "_key_rule_uri";
	public static final String KEY_PREFIX_REMOTE_CONTEXT_PARAM = "_key_remote_cp_pre_";
	public static final String KEY_PREFIX_REMOTE_INVOKE_PARAM = "_key_remote_invoke_pre_";
	
	public static final RuleParam NULL_RULE_PARAM = RuleParam.create();
	
	public static RuleParam create(){
		return new RuleParam();
	}
	
	public static void main(String[] args) {
		RuleParam p = RuleParam.create().put("aaa", 123);
		System.out.println(p.get("aaa"));
		System.out.println(p.getString("aaa"));
	}
}
