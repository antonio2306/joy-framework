package cn.joy.plugin.beetl;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.plugin.JoyPlugin;

@Plugin(key="beetl")
public class BeetlPlugin extends JoyPlugin<BeetlResourceBuilder, BeetlResource>{
	public static BeetlResourceBuilder builder(){
		return new BeetlResourceBuilder();
	}
	
	public static BeetlPlugin plugin(){
		return (BeetlPlugin)JoyManager.plugin("beetl");
	}
	
	@Override
	public void start() {
		this.mainResource = builder().build();
	}

	@Override
	public void stop() {
		
	}
	
}
