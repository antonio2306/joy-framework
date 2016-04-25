package cn.joy.plugin.quartz;

import cn.joy.framework.plugin.PluginResourceBuilder;

public class QuartzResourceBuilder extends PluginResourceBuilder<QuartzResource>{
	@Override
	public QuartzResource build() {
		QuartzResource resource = new QuartzResource(name, prop);
		return resource;
	}
	
}
