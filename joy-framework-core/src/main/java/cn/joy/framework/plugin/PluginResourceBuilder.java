package cn.joy.framework.plugin;

import java.util.Properties;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;

public abstract class PluginResourceBuilder<R extends PluginResource> {
	protected Log log = LogKit.getLog(PluginResourceBuilder.class);
	protected String name;
	protected Properties prop;
	
	public PluginResourceBuilder<R> prop(Properties prop){
		this.prop = prop;
		return this;
	}
	
	public PluginResourceBuilder<R> name(String name){
		this.name = name;
		return this;
	}
	
	R buildInternal(){
		R r = build();
		
		return r;
	}
	
	public abstract R build();
	
	public R buildTo(ResourcePlugin plugin){
		return (R)plugin.cacheResource(name, build());
	}
}
