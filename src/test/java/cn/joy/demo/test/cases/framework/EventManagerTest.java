package cn.joy.demo.test.cases.framework;

import org.testng.Assert;
import org.testng.annotations.Test;

import cn.joy.demo.center.module.user.event.UserUpdateEvent;
import cn.joy.demo.center.module.user.model.User;
import cn.joy.framework.event.EventManager;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.test.TestExecutor;

@Test(groups = "case.event", dependsOnGroups="case.init")
public class EventManagerTest {
	@Test(enabled=false)
	public static void main(String[] args) {
		TestExecutor.executeGroup("case.event");
	}

	public void publishUserUpdateEvent() {
		RuleContext rContext = RuleContext.createSingle("user1");
		User oldUser = new User();
		oldUser.setName("用户");
		User newUser = new User();
		newUser.setName("用户A");
		
		UserUpdateEvent event = new UserUpdateEvent(newUser, rContext).setOldUser(oldUser);
		EventManager.publishEvent(event);
		
		Assert.assertTrue(RuleKit.getBooleanParam(event.getRuleContext().getExtra().getDatas(), "nameChange"));
	}
}
