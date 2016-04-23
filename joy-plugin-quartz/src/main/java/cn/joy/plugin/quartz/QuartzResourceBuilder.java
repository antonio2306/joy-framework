package cn.joy.plugin.quartz;

import cn.joy.framework.plugin.PluginResourceBuilder;

public class QuartzResourceBuilder extends PluginResourceBuilder<QuartzResource>{
	private String name;
	
	public QuartzResourceBuilder name(String name){
		this.name = name;
		return this;
	}
	
	@Override
	public QuartzResource build() {
		QuartzResource resource = new QuartzResource(name, prop);
		return resource;
	}

}
