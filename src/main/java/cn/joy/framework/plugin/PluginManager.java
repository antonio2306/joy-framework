package cn.joy.framework.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyMap;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.ClassKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.provider.JoyProvider;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PluginManager{
	private static Logger logger = LoggerFactory.getLogger(PluginManager.class);
	private static final PluginManager me = new PluginManager();
	private JoyMap<String, JoyPlugin> plugins = new JoyMap<>();
	private JoyMap<Class<? extends JoyProvider>, JoyMap<String, JoyProvider>> providers = new JoyMap<>();
	
	private PluginManager(){
	}
	
	public static PluginManager build(){
		return me;
	}
	
	public void init(){
		logger.info("plugin manager init...");
		scanPlugin("cn.joy.plugin");
		
		if(logger.isDebugEnabled())
			logger.debug("plugins="+plugins+", providers="+providers);
	}
	
	public void scanPlugin(String packageName){
		Map<String, List<Class<? extends JoyPlugin>>> waitLoadPlugins = new HashMap<>();
		List<Class<? extends JoyPlugin>> pluginClassList = ClassKit.listClassBySuper(packageName, JoyPlugin.class);
		for(Class pluginClass:pluginClassList){
			loadPlugin(pluginClass, waitLoadPlugins);
		}
	}
	
	private void loadPlugin(Class<? extends JoyPlugin> pluginClass, Map<String, List<Class<? extends JoyPlugin>>> waitLoadPlugins){
		Plugin pluginInfo = pluginClass.getAnnotation(Plugin.class);
		boolean canLoad = true;
		for(String depend:pluginInfo.depends()){
			if(!plugins.containsKey(depend)){
				List<Class<? extends JoyPlugin>> waitLoadPluginList = waitLoadPlugins.get(depend);
				if(waitLoadPluginList==null){
					waitLoadPluginList = new ArrayList<Class<? extends JoyPlugin>>();
					waitLoadPlugins.put(depend, waitLoadPluginList);
				}
				waitLoadPluginList.add(pluginClass);
				canLoad = false;
				logger.info("Plugin["+pluginClass.getName()+"] wait "+depend);
			}
		}
		if(!canLoad)
			return;
			
		logger.info("Plugin["+pluginClass.getName()+"] load...");
		JoyPlugin plugin = (JoyPlugin)BeanKit.getNewInstance(pluginClass);
		if(plugin.init()){
			String pluginKey = pluginInfo.key();
			logger.info("Plugin[class="+pluginClass.getName()+", key="+pluginKey+"] init...");
			plugins.put(pluginKey, plugin);
			
			List<Class<? extends JoyProvider>> providerClassList = ClassKit.listClassBySuper(pluginClass.getPackage().getName(), JoyProvider.class);
			for(Class providerClass:providerClassList){
				logger.info("Provider["+providerClass.getName()+"] load...");
				JoyProvider provider = (JoyProvider)BeanKit.getNewInstance(providerClass);
				String providerKey = StringKit.rTrim(providerClass.getSimpleName().toLowerCase(), "provider");
				
				JoyMap<String, JoyProvider> providerMap = providers.get(providerClass.getSuperclass());
				if(providerMap==null){
					providerMap = new JoyMap<String, JoyProvider>();
					providers.put(providerClass.getSuperclass(), providerMap);
				}

				if(!providerMap.containsKey("default") || StringKit.isTrue(plugin.getConfig().get("provider."+providerKey+".default")))
					providerMap.put("default", provider);
				providerMap.put(providerKey, provider);
			}
			
			List<Class<? extends JoyPlugin>> waitLoadPluginList = waitLoadPlugins.get(pluginKey);
			if(waitLoadPluginList!=null){
				for(Class<? extends JoyPlugin> waitLoadPlugin: waitLoadPluginList){
					loadPlugin(waitLoadPlugin, waitLoadPlugins);
				}
				waitLoadPlugins.remove(pluginKey);
			}
		}
	}
	
	public JoyPlugin getPlugin(String pluginKey){
		return plugins.get(pluginKey);
	}
	
	public JoyProvider getProvider(Class<? extends JoyProvider> providerClass){
		return getProvider(providerClass, null);
	}
	
	public JoyProvider getProvider(Class<? extends JoyProvider> providerClass, String providerKey){
		return providers.get(providerClass).get(StringKit.getString(providerKey, "default"));
	}
	
	public void release() {
		for (Entry<String, JoyPlugin> entry : plugins.entry()) {
			entry.getValue().stop();
		}
		plugins.clear();
		
		for (Entry<Class<? extends JoyProvider>, JoyMap<String, JoyProvider>> entry : providers.entry()) {
			JoyMap<String, JoyProvider> providerMap = entry.getValue();
			for(Entry<String, JoyProvider> providerEntry:providerMap.entry()){
				providerEntry.getValue().release();
			}
		}
		providers.clear();
	}
}
