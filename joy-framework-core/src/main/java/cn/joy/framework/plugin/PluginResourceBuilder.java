package cn.joy.framework.plugin;

import java.util.Properties;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;

public abstract class PluginResourceBuilder<R extends PluginResource> {
	protected Log log = LogKit.getLog(PluginResourceBuilder.class);
	protected Properties prop;
	
	public PluginResourceBuilder<R> prop(Properties prop){
		this.prop = prop;
		return this;
	}
	
	public abstract R build();
}
