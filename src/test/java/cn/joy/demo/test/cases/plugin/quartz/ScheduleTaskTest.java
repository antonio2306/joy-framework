package cn.joy.demo.test.cases.plugin.quartz;

import org.testng.annotations.Test;

import cn.joy.framework.test.TestExecutor;
import cn.joy.plugin.quartz.Quartz;

@Test(groups="case.schedtask", dependsOnGroups="case.init")
public class ScheduleTaskTest {

	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executeGroup("case.schedtask");
	}
	
	public void testSchedtask(){
		Quartz.schedule(TestJob.class, "testJob", "test", "0/2 * * * * ?");
		
		try {
			System.out.println("主线程："+Thread.currentThread().getId());
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Quartz.unschedule("testJob", "test");
		Quartz.release();
	}
	
	
	/*public void testSchedtaskCluster(){
		QuartzPlugin schedPlugin = (QuartzPlugin)JoyManager.getPlugin("quartz");
		schedPlugin.use("testClusterScheduler", PropKit.use("quartz_cluster.properties").getProperties()).schedule(TestJob.class, "testJob", "testCluster", "0/3 * * * * ?");
		
		try {
			System.out.println("主线程："+Thread.currentThread().getId());
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		schedPlugin.use("testClusterScheduler").unschedule("testJob", "testCluster");
		schedPlugin.stop();
	}*/
	
}


