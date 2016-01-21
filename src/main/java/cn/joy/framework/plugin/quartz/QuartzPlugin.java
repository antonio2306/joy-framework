package cn.joy.framework.plugin.quartz;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import cn.joy.framework.kits.PathKit;
import cn.joy.framework.kits.PropKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.ISchedulePlugin;

public class QuartzPlugin implements ISchedulePlugin {
	private Logger logger = Logger.getLogger(QuartzPlugin.class);
	private QuartzScheduler mainScheduler;
	private Map<String, QuartzScheduler> schedulerMap = new HashMap();
	
	public QuartzScheduler use(String schedulerName){
		return use(schedulerName, null);
	}
	
	public QuartzScheduler use(String schedulerName, Properties prop){
		if(StringKit.isEmpty(schedulerName))
			return mainScheduler;
		QuartzScheduler quartzScheduler = schedulerMap.get(schedulerName);
		if(quartzScheduler==null){
			quartzScheduler = QuartzScheduler.create(schedulerName, prop);
			schedulerMap.put(schedulerName, quartzScheduler);
		}
		return quartzScheduler;
	}

	public void start() {
		File configFile = new File(PathKit.getClassPath()+"/quartz.properties");
		if(configFile.exists())
			mainScheduler = QuartzScheduler.create("joyScheduler", PropKit.use(configFile).getProperties());
		else
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
				logger.error("", e);
			}
		}
	}

}
