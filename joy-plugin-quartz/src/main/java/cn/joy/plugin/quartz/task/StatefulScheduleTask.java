package cn.joy.plugin.quartz.task;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public interface StatefulScheduleTask extends ScheduleTask{
	
}
