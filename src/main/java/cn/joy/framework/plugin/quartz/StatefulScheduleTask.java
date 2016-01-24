package cn.joy.framework.plugin.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public interface StatefulScheduleTask extends ScheduleTask{
	
}
