package cn.joy.plugin.quartz;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.ResourcePlugin;
import cn.joy.plugin.quartz.task.ScheduleTask;

@Plugin(key="quartz")
public class QuartzPlugin extends ResourcePlugin<QuartzResourceBuilder, QuartzResource>{
	public static QuartzResourceBuilder builder(){
		return new QuartzResourceBuilder();
	}
	
	public static QuartzPlugin plugin(){
		return (QuartzPlugin)JoyManager.plugin("quartz");
	}
	
	@Override
	public boolean start() {
		boolean clusterEnable = StringKit.isTrue(getConfig().get("cluster.enable"));
		if(!clusterEnable)
			getConfig().removeAll("org.quartz.jobStore").removeAll("org.quartz.dataSource");
		
		logger.debug("clusterEnable="+clusterEnable);
		this.mainResource = builder().name("joyScheduler").prop(getConfig()).build();
		logger.info("quartz plugin start success");
		return true;
	}
	
	@Override
	public void stop() {
		
	}
	
	public static QuartzResource use(){
		return plugin().useResource();
	}
	
	public static QuartzResource use(String name){
		return plugin().useResource(name);
	}
	
	public static void unuse(String name){
		plugin().unuseResource(name);
	}

	public static void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		use().schedule(jobClass, jobName, jobGroup, cron);
	}
	
	public static void schedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron, boolean triggerOnceAfterSchedule){
		use().schedule(jobClass, jobName, jobGroup, cron, triggerOnceAfterSchedule);
	}
	
	public static void trigger(String jobName, String jobGroup){
		use().trigger(jobName, jobGroup);
	}
	
	public static void reSchedule(Class<? extends ScheduleTask> jobClass, String jobName, String jobGroup, String cron){
		use().reSchedule(jobClass, jobName, jobGroup, cron);
	}
	
	public static void pauseAllSchedule(){
		use().pauseAllSchedule();
	}
	
	public static void pauseSchedule(String jobName, String jobGroup){
		use().pauseSchedule(jobName, jobGroup);
	}
	
	public static void resumeAllSchedule(){
		use().resumeAllSchedule();
	}
	
	public static void resumeSchedule(String jobName, String jobGroup){
		use().resumeSchedule(jobName, jobGroup);
	}
	
	public static void unschedule(String jobName, String jobGroup){
		use().unschedule(jobName, jobGroup);
	}
}
