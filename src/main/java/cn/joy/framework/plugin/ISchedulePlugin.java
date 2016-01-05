package cn.joy.framework.plugin;

import cn.joy.framework.task.ScheduleTask;

public interface ISchedulePlugin extends IPlugin {
	public void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron);

	public void unschedule(String jobName, String jobGroup);
	
	public void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron);
	
}
