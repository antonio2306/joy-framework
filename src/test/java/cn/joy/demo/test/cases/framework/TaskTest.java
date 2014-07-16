package cn.joy.demo.test.cases.framework;

import java.util.Vector;

import org.testng.Assert;
import org.testng.annotations.Test;

import cn.joy.framework.task.JoyTask;
import cn.joy.framework.task.TaskExecutor;
import cn.joy.framework.task.TaskQueue;
import cn.joy.framework.test.TestExecutor;

@Test(groups="case.task", dependsOnGroups="case.init")
public class TaskTest {
	private Vector<String> taskQueueResults = new Vector<String>();
	private Vector<String> taskExecutorResults = new Vector<String>();

	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executeGroup("case.task");
	}
	
	public void testTaskQueue(){
		JoyTask t1 = new JoyTask() {
			public void run() {
				taskQueueResults.add("t1");
			}
		};
		JoyTask t2 = new JoyTask() {
			public void run() {
				taskQueueResults.add("t2");
			}
		};
		JoyTask t3 = new JoyTask() {
			public void run() {
				taskQueueResults.add("t3");
			}
		};
		TaskQueue tq = new TaskQueue();
		tq.execute(t1);
		tq.execute(t2);
		tq.execute(t3);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		
		Assert.assertEquals(taskQueueResults.toArray(), new Object[]{"t1","t2","t3"});
	}
	
	public void testTaskExecutor(){
		JoyTask t1 = new JoyTask() {
			public void run() {
				taskExecutorResults.add("t1");
			}
		};
		JoyTask t2 = new JoyTask() {
			public void run() {
				taskExecutorResults.add("t2");
			}
		};
		JoyTask t3 = new JoyTask() {
			public void run() {
				taskExecutorResults.add("t3");
			}
		};
		TaskExecutor.execute(t1);
		TaskExecutor.execute(t2);
		TaskExecutor.execute(t3);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		
		//顺序不一定
		//Assert.assertNotEquals(taskExecutorResults.toArray(), new Object[]{"t1","t2","t3"});
	}
}
