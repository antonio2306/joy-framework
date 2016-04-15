package cn.joy.framework.plugin;

import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.joy.framework.core.JoyMap;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.ClassKit;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PluginManager{
	private static Logger logger = LoggerFactory.getLogger(PluginManager.class);
	private static final PluginManager me = new PluginManager();
	private JoyMap<String, JoyPlugin> plugins = new JoyMap<>();
	private JoyMap<Class<? extends JoyExtension>, JoyExtension> exts = new JoyMap<>();
	
	private PluginManager(){
		this.init();
	}
	
	public static PluginManager build(){
		return me;
	}
	
	public void init(){
		logger.info("plugin manager init...");
		scanPlugin("cn.joy.plugin");
	}
	
	public void scanPlugin(String packageName){
		List<Class<? extends JoyPlugin>> pluginClassList = ClassKit.listClassBySuper(packageName, JoyPlugin.class);
		for(Class pluginClass:pluginClassList){
			logger.info("Plugin["+pluginClass.getName()+"] load...");
			JoyPlugin plugin = (JoyPlugin)BeanKit.getNewInstance(pluginClass);
			String pluginKey = pluginClass.getName().toLowerCase().replace(packageName+".", "");
			if(pluginKey.endsWith("plugin"))
				pluginKey = pluginKey.substring(0, pluginKey.length()-6);
			if(plugin.init()){
				logger.info("Plugin["+pluginClass.getName()+"] init...");
				plugins.put(pluginKey, plugin);
				
				List<Class<? extends JoyExtension>> extClassList = ClassKit.listClassBySuper(pluginClass.getPackage().getName(), JoyExtension.class);
				for(Class extClass:extClassList){
					logger.info("Extension["+extClass.getName()+"] load...");
					JoyExtension extension = (JoyExtension)BeanKit.getNewInstance(extClass);
					exts.put(extClass.getSuperclass(), extension);
				}
			}
		}
	}
	
	public JoyPlugin getPlugin(String pluginKey){
		return plugins.get(pluginKey);
	}
	
	public JoyExtension getExtension(Class<? extends JoyExtension> extClass){
		return exts.get(extClass);
	}
	
	public void release() {
		for (Entry<String, JoyPlugin> entry : plugins.entry()) {
			entry.getValue().stop();
		}
		plugins.clear();
		
	}
}
