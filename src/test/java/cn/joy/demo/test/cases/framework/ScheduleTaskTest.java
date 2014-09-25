package cn.joy.demo.test.cases.framework;

import org.testng.annotations.Test;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.plugin.ISchedulePlugin;
import cn.joy.framework.test.TestExecutor;

@Test(groups="case.schedtask", dependsOnGroups="case.init")
public class ScheduleTaskTest {

	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executeGroup("case.schedtask");
	}
	
	public void testSchedtask(){
		ISchedulePlugin schedPlugin = (ISchedulePlugin)JoyManager.getPlugin("quartz");
		schedPlugin.schedule(TestJob.class, "testJob", "test", "0/5 * * * * ?");
		
		try {
			System.out.println("主线程："+Thread.currentThread().getId());
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		schedPlugin.unschedule("testJob", "test");
		
		schedPlugin.stop();
	}
	
}


