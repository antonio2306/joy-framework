package cn.joy.demo.test;

import cn.joy.demo.center.module.user.event.UserUpdateEvent;
import cn.joy.demo.center.module.user.model.User;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.event.EventManager;
import cn.joy.framework.rule.RuleContext;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		//JoyManager jm = new JoyManager();
		JoyManager.init();
		
		RuleContext rContext = RuleContext.createSingle("user1");
		User oldUser = new User();
		oldUser.setName("用户");
		User newUser = new User();
		newUser.setName("用户A");
		EventManager.publishEvent(new UserUpdateEvent(newUser, rContext).setOldUser(oldUser));
	}
}
