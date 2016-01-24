package cn.joy.framework.plugin;

import cn.joy.framework.plugin.quartz.ScheduleTask;

public interface ISchedulePlugin extends IPlugin {
	public void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron);

	public void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron);
	
	public void pauseAllSchedule();
	
	public void pauseSchedule(String jobName, String jobGroup);
	
	public void resumeAllSchedule();
	
	public void resumeSchedule(String jobName, String jobGroup);
	
	public void unschedule(String jobName, String jobGroup);
	
}
