package cn.joy.framework.plugin;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.PropKit.Prop;

public abstract class PluginResourceBuilder<R extends PluginResource> {
	protected Log logger = LogKit.getLog(PluginResourceBuilder.class);
	protected String name;
	protected Prop prop;
	
	public PluginResourceBuilder<R> prop(Prop prop){
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
