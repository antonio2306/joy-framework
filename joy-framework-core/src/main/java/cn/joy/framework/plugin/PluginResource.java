package cn.joy.framework.plugin;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;

public abstract class PluginResource {
	protected Log logger = LogKit.get();
	protected String resourceName;
	
	//public abstract void init(String name, Properties prop);
	public abstract void release();
}
