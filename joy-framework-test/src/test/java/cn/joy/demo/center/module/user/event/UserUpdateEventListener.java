package cn.joy.demo.center.module.user.event;

import org.apache.log4j.Logger;

import cn.joy.demo.center.module.user.model.User;
import cn.joy.framework.event.JoyEventListener;

public class UserUpdateEventListener implements JoyEventListener<UserUpdateEvent> {
	private Logger logger = Logger.getLogger(UserUpdateEventListener.class);

	public int getOrder() {
		return 0;
	}

	public void onJoyEvent(UserUpdateEvent event) {
		System.out.println("UserUpdateEvent...");
		User newUser = (User)event.getSource();
		User oldUser = event.getOldUser();
		if(newUser==null || oldUser==null){
			logger.warn("event user info is null");
			return;
		}
			
		if(!newUser.getName().equals(oldUser.getName())){
			event.getEventContext().put("nameChange", true);
		}
	}

}
