package cn.joy.demo.test;

import cn.joy.demo.center.module.user.event.UserUpdateEvent;
import cn.joy.demo.center.module.user.model.User;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.event.EventManager;
import cn.joy.framework.rule.RuleContext;
import cn.joy.framework.rule.RuleExecutor;
import cn.joy.framework.rule.RuleInvokeConfig;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		//JoyManager jm = new JoyManager();
		JoyManager.init();
		
		RuleContext rContext = RuleContext.createSingle("user1");
		
		RuleResult ruleResult = RuleExecutor.create(rContext).execute("user.userService#testAsyn", 
				RuleParam.create());
		System.out.println(ruleResult.isSuccess());
		System.out.println(ruleResult.getMsg());
	}
}
