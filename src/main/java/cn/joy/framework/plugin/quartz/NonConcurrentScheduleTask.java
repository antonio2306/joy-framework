package cn.joy.framework.plugin.quartz;

import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public interface NonConcurrentScheduleTask extends ScheduleTask{

}
