package cn.joy.demo.center.module.user;

import cn.joy.demo.center.module.user.job.UserDataClear;
import cn.joy.framework.annotation.Module;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.core.JoyModule;
import cn.joy.framework.plugin.ISchedulePlugin;

@Module(name="用户模块", code="usercenter", desc="用户注册、登录、管理中心")
public class UserModule extends JoyModule{
	
	@Override
	public void initModule(){
		ISchedulePlugin schedulePlugin = (ISchedulePlugin)JoyManager.getPlugin("quartz");
		if(schedulePlugin!=null){
			schedulePlugin.schedule(UserDataClear.class, "clearUserDatas", "user.clean", "0/5 * * * * ?");
		}
	}

}
