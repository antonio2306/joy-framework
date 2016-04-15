package cn.joy.plugin.quartz;

import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public interface NonConcurrentScheduleTask extends ScheduleTask{

}
