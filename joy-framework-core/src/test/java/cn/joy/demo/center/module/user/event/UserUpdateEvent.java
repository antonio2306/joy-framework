package cn.joy.demo.center.module.user.event;

import cn.joy.demo.center.module.user.model.User;
import cn.joy.framework.event.JoyEvent;
import cn.joy.framework.rule.RuleContext;

public class UserUpdateEvent extends JoyEvent {
	private User oldUser;

	public UserUpdateEvent(Object source, RuleContext rContext) {
		super(source, rContext);
	}

	public User getOldUser() {
		return oldUser;
	}

	public UserUpdateEvent setOldUser(User oldUser) {
		this.oldUser = oldUser;
		return this;
	}

}
