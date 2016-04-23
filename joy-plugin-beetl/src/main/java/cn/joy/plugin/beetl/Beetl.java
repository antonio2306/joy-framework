package cn.joy.plugin.beetl;

import java.io.File;
import java.util.Map;

public class Beetl{
	public static BeetlResourceBuilder builder(){
		return BeetlPlugin.builder();
	}
	
	public static BeetlResource use(String name){
		return BeetlPlugin.plugin().use(name);
	}
	
	public static void merge(String tpl, Map<String, Object> datas, File outFile){
		BeetlPlugin.plugin().use().merge(tpl, datas, outFile);
	}
	
}
