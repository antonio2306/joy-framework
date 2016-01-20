package cn.joy.framework.plugin.quartz;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.ISchedulePlugin;

public class QuartzPlugin implements ISchedulePlugin {
	private Logger logger = Logger.getLogger(QuartzPlugin.class);
	private QuartzScheduler mainScheduler;
	private Map<String, QuartzScheduler> schedulerMap = new HashMap();
	
	public QuartzScheduler use(String schedulerName){
		if(StringKit.isEmpty(schedulerName))
			return mainScheduler;
		QuartzScheduler quartzScheduler = schedulerMap.get(schedulerName);
		if(quartzScheduler==null){
			quartzScheduler = QuartzScheduler.create(schedulerName);
			schedulerMap.put(schedulerName, quartzScheduler);
		}
		return quartzScheduler;
	}

	public void start() {
		mainScheduler = QuartzScheduler.create("joyScheduler");
		schedulerMap.put("joyScheduler", mainScheduler);
		if(logger.isInfoEnabled())
			logger.info("quartz plugin start success");
	}
	
	public void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		mainScheduler.schedule(jobClass, jobName, jobGroup, cron);
	}
	
	public void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		mainScheduler.reSchedule(jobClass, jobName, jobGroup, cron);
	}
	
	public void pauseAllSchedule(){
		mainScheduler.pauseAllSchedule();
	}
	
	public void pauseSchedule(String jobName, String jobGroup){
		mainScheduler.pauseSchedule(jobName, jobGroup);
	}
	
	public void resumeAllSchedule(){
		mainScheduler.resumeAllSchedule();
	}
	
	public void resumeSchedule(String jobName, String jobGroup){
		mainScheduler.resumeSchedule(jobName, jobGroup);
	}
	
	public void unschedule(String jobName, String jobGroup){
		mainScheduler.unschedule(jobName, jobGroup);
	}

	public void stop() {
		for(Entry<String, QuartzScheduler> entry:schedulerMap.entrySet()){
			try {
				entry.getValue().getScheduler().shutdown();
			} catch (SchedulerException e) {
				
			}
		}
	}

}
