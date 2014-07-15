package cn.joy.demo.center.module.user.rule;

import cn.joy.framework.rule.BaseRule;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;

public class UserServiceRule extends BaseRule{
	public RuleResult getUser(RuleContext rContext, RuleParam rParam) throws Exception{
		RuleResult ruleResult = RuleResult.create();
		
		logger.debug("getUser...");
		//rContext.invokeRule("userTest#testError", rParam);
		rContext.invokeRule("userTest#testOK", rParam);
		
		return ruleResult.success();
	}
}
