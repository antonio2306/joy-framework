package cn.joy.plugin.quartz;

import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import cn.joy.framework.kits.StringKit;
import cn.joy.plugin.quartz.task.ScheduleTask;

public class Quartz{
	private static Logger logger = Logger.getLogger(Quartz.class);
	static QuartzScheduler mainScheduler = QuartzScheduler.nullQuartzScheduler;
	private static final ConcurrentHashMap<String, QuartzScheduler> schedulerMap = new ConcurrentHashMap<String, QuartzScheduler>();
	
	public static QuartzScheduler use(String schedulerName){
		return use(schedulerName, null);
	}
	
	public static QuartzScheduler use(String schedulerName, Properties prop){
		if(StringKit.isEmpty(schedulerName))
			return mainScheduler;
		QuartzScheduler quartzScheduler = schedulerMap.get(schedulerName);
		if(quartzScheduler==null){
			quartzScheduler = QuartzScheduler.create(schedulerName, prop);
			schedulerMap.put(schedulerName, quartzScheduler);
		}
		return quartzScheduler;
	}
	
	public static void unuse(String schedulerName){
		if(StringKit.isEmpty(schedulerName))
			return;
		QuartzScheduler quartzScheduler = schedulerMap.get(schedulerName);
		if(quartzScheduler!=null){
			quartzScheduler.shutdown();
		}
		schedulerMap.remove(schedulerName);
	}

	public static void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		mainScheduler.schedule(jobClass, jobName, jobGroup, cron);
	}
	
	public static void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		mainScheduler.reSchedule(jobClass, jobName, jobGroup, cron);
	}
	
	public static void pauseAllSchedule(){
		mainScheduler.pauseAllSchedule();
	}
	
	public static void pauseSchedule(String jobName, String jobGroup){
		mainScheduler.pauseSchedule(jobName, jobGroup);
	}
	
	public static void resumeAllSchedule(){
		mainScheduler.resumeAllSchedule();
	}
	
	public static void resumeSchedule(String jobName, String jobGroup){
		mainScheduler.resumeSchedule(jobName, jobGroup);
	}
	
	public static void unschedule(String jobName, String jobGroup){
		mainScheduler.unschedule(jobName, jobGroup);
	}

	public static void release(){
		for(Entry<String, QuartzScheduler> entry:schedulerMap.entrySet()){
			try {
				entry.getValue().getScheduler().shutdown();
			} catch (SchedulerException e) {
				logger.error("", e);
			}
		}
	}
}
