package cn.joy.demo.center.module.user.rule;

import cn.joy.framework.rule.BaseRule;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;

public class UserTestRule extends BaseRule{
	public RuleResult testOK(RuleContext rContext, RuleParam rParam) throws Exception{
		RuleResult ruleResult = RuleResult.create();
		
		logger.debug("testOK...");
		
		return ruleResult.success("test ok");
	}
	
	public RuleResult testError(RuleContext rContext, RuleParam rParam) throws Exception{
		RuleResult ruleResult = RuleResult.create();
		
		logger.debug("testError...");
		
		return ruleResult.fail("test error");
	}
}
