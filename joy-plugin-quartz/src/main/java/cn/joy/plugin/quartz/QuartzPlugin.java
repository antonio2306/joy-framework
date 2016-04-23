package cn.joy.plugin.quartz;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.JoyPlugin;

@Plugin(key="quartz")
public class QuartzPlugin extends JoyPlugin<QuartzResourceBuilder, QuartzResource>{
	public static QuartzResourceBuilder builder(){
		return new QuartzResourceBuilder();
	}
	
	public static QuartzPlugin plugin(){
		return (QuartzPlugin)JoyManager.plugin("quartz");
	}
	
	@Override
	public void start() {
		boolean clusterEnable = StringKit.isTrue(getConfig().get("cluster.enable"));
		if(!clusterEnable)
			getConfig().removeAll("org.quartz.jobStore").removeAll("org.quartz.dataSource");
		
		log.debug("clusterEnable="+clusterEnable);
		this.mainResource = builder().name("joyScheduler").prop(getConfig().getProperties()).build();
		log.info("quartz plugin start success");
	}
	
	@Override
	public void stop() {
		
	}

}
