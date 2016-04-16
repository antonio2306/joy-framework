package cn.joy.plugin.quartz;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.JoyPlugin;

@Plugin(key="quartz")
public class QuartzPlugin extends JoyPlugin{
	public void start() {
		boolean clusterEnable = StringKit.isTrue(getConfig().get("cluster.enable"));
		if(!clusterEnable)
			getConfig().removeAll("org.quartz.jobStore").removeAll("org.quartz.dataSource");
		
		log.debug("clusterEnable="+clusterEnable);
		Quartz.mainScheduler = Quartz.use("joyScheduler", getConfig().getProperties());
		log.info("quartz plugin start success");
	}
	
	
	public void stop() {
		Quartz.release();
	}

}
