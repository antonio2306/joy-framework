package cn.joy.plugin.quartz.task;

import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public interface NonConcurrentScheduleTask extends ScheduleTask{

}
