package cn.joy.demo.center.module.user.rule;

import javax.servlet.http.HttpServletRequest;

import cn.joy.demo.test.cases.framework.RuleExecutorTest;
import cn.joy.framework.rule.BaseRule;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;

public class UserTestRule extends BaseRule{
	public RuleResult testOK(RuleContext rContext, RuleParam rParam, HttpServletRequest request) throws Exception{
		RuleResult ruleResult = RuleResult.create();
		
		logger.debug("testOK, user="+rContext.getLoginId());
		
		return ruleResult.success("test ok");
	}
	
	public RuleResult testOKAsyn(RuleContext rContext, RuleParam rParam, HttpServletRequest request) throws Exception{
		RuleResult ruleResult = RuleResult.create();
		
		logger.debug("testOKAsyn, user="+rContext.getLoginId());
		RuleExecutorTest.asynResultMap.put("testAsyn", "OK");
		
		return ruleResult.success("test ok asyn");
	}
	
	public RuleResult testError(RuleContext rContext, RuleParam rParam, HttpServletRequest request) throws Exception{
		RuleResult ruleResult = RuleResult.create();
		
		logger.debug("testError, user="+rContext.getLoginId());
		
		return ruleResult.fail("test error");
	}
}
