package cn.joy.plugin.test.quartz;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.joy.plugin.quartz.task.ScheduleTask;

public class TestJob implements ScheduleTask {  
    //把要执行的操作，写在execute方法中  
    public void execute(JobExecutionContext arg0) throws JobExecutionException {  
    	System.out.println("测试任务调度，线程："+Thread.currentThread().getId());
        System.out.println("测试Quartz："+new Date());  
    }  
    
}  
