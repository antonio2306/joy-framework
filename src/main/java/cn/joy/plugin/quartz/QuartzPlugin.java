package cn.joy.plugin.quartz;

import org.apache.log4j.Logger;

import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.JoyPlugin;

public class QuartzPlugin extends JoyPlugin{
	private Logger logger = Logger.getLogger(QuartzPlugin.class);
	
	public void start() {
		boolean clusterEnable = StringKit.isTrue(getConfig().get("cluster.enable"));
		if(!clusterEnable)
			getConfig().removeAll("org.quartz.jobStore").removeAll("org.quartz.dataSource");
		
		logger.debug("clusterEnable="+clusterEnable);
		logger.debug(getConfig().getProperties());
		
		Quartz.mainScheduler = Quartz.use("joyScheduler", getConfig().getProperties());
		
		logger.info("quartz plugin start success");
	}
	
	
	public void stop() {
		Quartz.release();
	}

}
