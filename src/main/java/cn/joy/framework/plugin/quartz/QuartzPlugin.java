package cn.joy.framework.plugin.quartz;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import cn.joy.framework.plugin.ISchedulePlugin;
import cn.joy.framework.task.ScheduleTask;

public class QuartzPlugin implements ISchedulePlugin {
	private Logger logger = Logger.getLogger(QuartzPlugin.class);
	private Scheduler scheduler;

	public void start() {
		String schedulerName = "joyScheduler";

		Properties quartzProp = new Properties();
		quartzProp.put("org.quartz.scheduler.instanceName", schedulerName);
		quartzProp.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		quartzProp.put("org.quartz.threadPool.threadCount", "10");
		quartzProp.put("org.quartz.threadPool.threadPriority", "5");
		quartzProp.put("org.quartz.jobStore.misfireThreshold", "60000");
		quartzProp.put("org.quartz.scheduler.skipUpdateCheck", true);

		try {
			StdSchedulerFactory schedFact = new StdSchedulerFactory(quartzProp);
			scheduler = schedFact.getScheduler();
			scheduler.start();
			if(logger.isInfoEnabled())
				logger.info("quartz plugin start success");
		} catch (SchedulerException e) {
			throw new RuntimeException("init scheduler fail.", e);
		}
	}
	
	public void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		try {
			JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build(); 
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName+"Trigger", jobGroup)
					.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException("schedule job fail.", e);
		}
	}
	
	public void unschedule(String jobName, String jobGroup){
		try {
			scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException e) {
			throw new RuntimeException("unschedule job fail.", e);
		}
	}

	public void stop() {
		try {
			if(scheduler!=null)
				scheduler.shutdown(true);
		} catch (SchedulerException e) {
			throw new RuntimeException("stop scheduler fail.", e);
		}
		
	}

}
