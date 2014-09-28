package cn.joy.demo.center.module.user.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.joy.framework.task.ScheduleTask;

public class UserDataClear implements ScheduleTask{
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("run user data clear job...");
	}
}
