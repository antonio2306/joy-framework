package cn.joy.plugin.beetl;

import java.io.OutputStream;
import java.util.Map;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.plugin.ResourcePlugin;

@Plugin(key="beetl")
public class BeetlPlugin extends ResourcePlugin<BeetlResourceBuilder, BeetlResource>{
	public static BeetlResourceBuilder builder(){
		return new BeetlResourceBuilder();
	}
	
	public static BeetlPlugin plugin(){
		return (BeetlPlugin)JoyManager.plugin(BeetlPlugin.class);
	}
	
	public static BeetlResource use(){
		return plugin().useResource();
	}
	
	public static BeetlResource use(String name){
		return plugin().useResource(name);
	}
	
	public static void unuse(String name){
		plugin().unuseResource(name);
	}
	
	@Override
	public boolean start() {
		this.mainResource = builder().build();
		return true;
	}

	@Override
	public void stop() {
		
	}
	
	public static void merge(String tpl, Map<String, Object> datas, OutputStream outputStream){
		use().merge(tpl, datas, outputStream);
	}
}
