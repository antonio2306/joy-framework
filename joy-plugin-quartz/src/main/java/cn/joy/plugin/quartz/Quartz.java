package cn.joy.plugin.quartz;

import java.util.Map.Entry;

import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;

import cn.joy.plugin.quartz.task.ScheduleTask;

public class Quartz{
	public static QuartzPlugin plugin(){
		return QuartzPlugin.plugin();
	}
	
	public static QuartzResourceBuilder builder(){
		return QuartzPlugin.builder();
	}
	
	public static QuartzResource use(String name){
		return QuartzPlugin.plugin().use(name);
	}
	
	public static void unuse(String name){
		QuartzPlugin.plugin().unuse(name);
	}

	public static void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		QuartzPlugin.plugin().use().schedule(jobClass, jobName, jobGroup, cron);
	}
	
	public static void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron, boolean triggerOnceAfterSchedule){
		QuartzPlugin.plugin().use().schedule(jobClass, jobName, jobGroup, cron, triggerOnceAfterSchedule);
	}
	
	public static void trigger(String jobName, String jobGroup){
		QuartzPlugin.plugin().use().trigger(jobName, jobGroup);
	}
	
	public static void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		QuartzPlugin.plugin().use().reSchedule(jobClass, jobName, jobGroup, cron);
	}
	
	public static void pauseAllSchedule(){
		QuartzPlugin.plugin().use().pauseAllSchedule();
	}
	
	public static void pauseSchedule(String jobName, String jobGroup){
		QuartzPlugin.plugin().use().pauseSchedule(jobName, jobGroup);
	}
	
	public static void resumeAllSchedule(){
		QuartzPlugin.plugin().use().resumeAllSchedule();
	}
	
	public static void resumeSchedule(String jobName, String jobGroup){
		QuartzPlugin.plugin().use().resumeSchedule(jobName, jobGroup);
	}
	
	public static void unschedule(String jobName, String jobGroup){
		QuartzPlugin.plugin().use().unschedule(jobName, jobGroup);
	}

}
