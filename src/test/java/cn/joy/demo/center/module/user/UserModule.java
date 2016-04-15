package cn.joy.demo.center.module.user;

import cn.joy.demo.center.module.user.job.UserDataClear;
import cn.joy.framework.annotation.Module;
import cn.joy.framework.core.JoyModule;
import cn.joy.plugin.quartz.Quartz;

@Module(name="用户模块", code="usercenter", desc="用户注册、登录、管理中心")
public class UserModule extends JoyModule{
	
	@Override
	public void initModule(){
		Quartz.schedule(UserDataClear.class, "clearUserDatas", "user.clean", "0/5 * * * * ?");
	}

}
