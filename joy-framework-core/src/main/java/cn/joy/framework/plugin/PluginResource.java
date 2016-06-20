package cn.joy.framework.plugin;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.PropKit.Prop;

public abstract class PluginResource {
	protected Log logger = LogKit.get();
	protected String name;
	protected Prop prop;
	
	public String getName(){
		return this.name;
	}
	
	public Prop getConfig(){
		return this.prop;
	}
	
	//public abstract void init(String name, Properties prop);
	public abstract void release();
}
