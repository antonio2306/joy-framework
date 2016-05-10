package cn.joy.demo.test.cases.framework;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;
import cn.joy.framework.test.TestExecutor;

@Test(groups = "case.executor", dependsOnGroups = "case.init")
public class RuleExecutorTest {
	public static Map<String, String> asynResultMap = new HashMap<>();
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executeGroup("case.executor");
	}
	
	public void executeLocalRule() {
		RuleResult ruleResult = JoyManager.getRuleExecutor().execute(RuleContext.create().user("user1").ruleURI("user.userService#testSyn"), 
				RuleParam.create().put("param1", "p1"));
		Assert.assertTrue(ruleResult.isSuccess());
	}

	public void executeLocalAsynRule() {
		RuleResult ruleResult = JoyManager.getRuleExecutor().execute(RuleContext.create().user("user1").ruleURI("user.userService#testAsyn"), 
				RuleParam.create().put("param1", "p1"));
		Assert.assertTrue(ruleResult.isSuccess());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		Assert.assertEquals(asynResultMap.get("testAsyn"), "OK");
	}
}
