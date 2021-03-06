package cn.joy.plugin.quartz;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import cn.joy.plugin.quartz.task.ScheduleTask;

public class QuartzScheduler {
	private Logger logger = Logger.getLogger(QuartzScheduler.class);
	private Scheduler scheduler;
	public final static QuartzScheduler nullQuartzScheduler = new QuartzScheduler();
	
	private QuartzScheduler(){}
	
	public static QuartzScheduler create(String schedulerName){
		return create(schedulerName, null);
	}
	
	public static QuartzScheduler create(String schedulerName, Properties prop){
		Properties quartzProp = new Properties();
		quartzProp.put("org.quartz.scheduler.instanceName", schedulerName);
		quartzProp.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		quartzProp.put("org.quartz.threadPool.threadCount", "10");
		quartzProp.put("org.quartz.threadPool.threadPriority", "5");
		quartzProp.put("org.quartz.jobStore.misfireThreshold", "60000");
		quartzProp.put("org.quartz.scheduler.skipUpdateCheck", true);
		
		if(prop!=null)
			quartzProp.putAll(prop);

		QuartzScheduler quartzScheduler = new QuartzScheduler();
		try {
			StdSchedulerFactory schedFact = new StdSchedulerFactory(quartzProp);
			quartzScheduler.scheduler = schedFact.getScheduler();
			quartzScheduler.scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException("create scheduler fail.", e);
		}
		return quartzScheduler;
	}
	
	public Scheduler getScheduler(){
		return scheduler;
	}
	
	public void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		schedule(jobClass, jobName, jobGroup, cron, null, false);
	}
	
	public void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron, boolean triggerOnceAfterSchedule){
		schedule(jobClass, jobName, jobGroup, cron, null, triggerOnceAfterSchedule);
	}
	
	public void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron, Map<String, Object> datas, boolean triggerOnceAfterSchedule){
		if(scheduler==null)
			return;
		try {
			//if(scheduler.checkExists(JobKey.jobKey(jobName, jobGroup)))
			//	return;
			JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build(); 
			if(datas!=null)
				job.getJobDataMap().putAll(datas);
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName+"Trigger", jobGroup)
					.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
			Set<Trigger> triggers = new HashSet<Trigger>();
			triggers.add(trigger);
			scheduler.scheduleJob(job, triggers, true);//.scheduleJob(job, trigger);
			if(triggerOnceAfterSchedule)
				scheduler.triggerJob(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			throw new RuntimeException("schedule job fail.", e);
		}
	}
	
	public void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		reSchedule(jobClass, jobName, jobGroup, cron, null, false);
	}
	
	public void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron, boolean triggerOnceAfterSchedule){
		reSchedule(jobClass, jobName, jobGroup, cron, null, triggerOnceAfterSchedule);
	}
	
	public void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron, Map<String, Object> datas, boolean triggerOnceAfterSchedule){
		if(scheduler==null)
			return;
		try {
			if(scheduler.checkExists(JobKey.jobKey(jobName, jobGroup)))
				unschedule(jobName, jobGroup);
			schedule(jobClass, jobName, jobGroup, cron, datas, triggerOnceAfterSchedule);
		} catch (SchedulerException e) {
			throw new RuntimeException("reSchedule job fail.", e);
		}
	}
	
	public void pauseSchedule(String jobName, String jobGroup){
		if(scheduler==null)
			return;
		try {
			scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			throw new RuntimeException("pauseSchedule job fail.", e);
		}
	}
	
	public void pauseAllSchedule(){
		if(scheduler==null)
			return;
		try {
			scheduler.pauseAll();
		} catch (SchedulerException e) {
			throw new RuntimeException("pauseAllSchedule job fail.", e);
		}
	}
	
	public void resumeSchedule(String jobName, String jobGroup){
		if(scheduler==null)
			return;
		try {
			scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			throw new RuntimeException("pauseSchedule job fail.", e);
		}
	}
	
	public void resumeAllSchedule(){
		if(scheduler==null)
			return;
		try {
			scheduler.resumeAll();
		} catch (SchedulerException e) {
			throw new RuntimeException("pauseAllSchedule job fail.", e);
		}
	}
	
	public void trigger(String jobName, String jobGroup){
		if(scheduler==null)
			return;
		try {
			scheduler.triggerJob(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			throw new RuntimeException("trigger job fail.", e);
		}
	}
	
	public void unschedule(String jobName, String jobGroup){
		if(scheduler==null)
			return;
		try {
			scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			throw new RuntimeException("unschedule job fail.", e);
		}
	}
	
	public void shutdown(){
		if(scheduler==null)
			return;
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			logger.error("scheduler shutdown fail.", e);
		}
	}
}
