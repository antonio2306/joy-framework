package cn.joy.framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.joy.framework.annotation.Module;
import cn.joy.framework.event.EventManager;
import cn.joy.framework.event.JoyEventListener;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.ClassKit;
import cn.joy.framework.kits.PathKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.IPlugin;
import cn.joy.framework.plugin.ITransactionPlugin;
import cn.joy.framework.rule.RuleLoader;
import cn.joy.framework.server.AppServer;
import cn.joy.framework.server.CenterServer;
import cn.joy.framework.server.JoyServer;
import cn.joy.framework.support.AppAuthManager;
import cn.joy.framework.support.DefaultAppAuthManager;
import cn.joy.framework.support.DefaultRouteStore;
import cn.joy.framework.support.DefaultSecurityManager;
import cn.joy.framework.support.RouteStore;
import cn.joy.framework.support.SecurityManager;

/**
 * 框架管理器，负责启动时的初始化工作
 * @author liyy
 * @date 2014-05-20
 */
public class JoyManager {
	private static Logger logger = Logger.getLogger(JoyManager.class);
	private static Map<String, IPlugin> plugins = new HashMap();
	private static List<String> modules = new ArrayList();
	private static Map<String, JoyModule> moduleDefines = new HashMap();
	
	private static RuleLoader rLoader;
	private static JoyServer server;
	private static ITransactionPlugin txPlugin;
	private static SecurityManager securityManager;
	private static AppAuthManager appAuthManager;
	private static RouteStore routeStore;
	
	public static SecurityManager getSecurityManager() {
		if(securityManager==null)
			securityManager = new DefaultSecurityManager();
		return securityManager;
	}

	public static void setSecurityManager(SecurityManager securityManager) {
		JoyManager.securityManager = securityManager;
	}
	
	public static AppAuthManager getAppAuthManager() {
		if(appAuthManager==null)
			appAuthManager = new DefaultAppAuthManager();
		return appAuthManager;
	}

	public static void setAppAuthManager(AppAuthManager appAuthManager) {
		JoyManager.appAuthManager = appAuthManager;
	}

	public static RouteStore getRouteStore() {
		if(routeStore==null)
			routeStore = new DefaultRouteStore();
		return routeStore;
	}

	public static void setRouteStore(RouteStore routeStore) {
		JoyManager.routeStore = routeStore;
	}
	
	public static RuleLoader getRuleLoader() {
		return rLoader;
	}
	
	public static ITransactionPlugin getTransactionPlugin() {
		return txPlugin;
	}
	
	public static JoyServer getServer(){
		if(server==null){
			server = new AppServer();
			server.setVariable("app_local_server_type", "none");
		}
		return server;
	}
	
	public static JoyModule getModule(String name){
		return moduleDefines.get(name);
	}

	public static void init() throws Exception{
		logger.info("JOY Framework start...");
		
		Properties config = new Properties();
		
		boolean isCenterServer = true;
		String classPath = PathKit.getClassPath();	//建议路径中不要含有空格等，否则需要decode
		
		File configFile = new File(classPath+"/joy-center.cnf");
		
		if(!configFile.exists()){
			configFile = new File(classPath+"/joy-app.cnf");
			isCenterServer = false;
		}
		
		if(!configFile.exists()){
			//throw new RuntimeException("No JOY config file exists!");
			//没有配置文件则不启用JOY框架
			logger.warn("No JOY config file exists!");
			return;
		}
		
		config.load(new FileInputStream(configFile));
		server = isCenterServer?new CenterServer():new AppServer();
		server.init(config);
		
		txPlugin = (ITransactionPlugin)loadPlugin(server.getTransactionPlugin());
		
		String optionalPlugins = server.getPlugins();
		if(StringKit.isNotEmpty(optionalPlugins)){
			String[] ops = optionalPlugins.split(",");
			for(String op:ops){
				loadOptionalPlugin(op);
			}
		}
		
		for(IPlugin plugin:plugins.values()){
			plugin.start();
		}
		
		rLoader = RuleLoader.singleton();
		
		getRouteStore().initRoute();
		
		scanModules();
		
		logger.info("JOY Framework run...");
	}
	
	public static void destroy(){
		logger.info("JOY Framework stop...");
		
		for(JoyModule module:moduleDefines.values()){
			logger.info("destroy module "+module.getName()+"...");
			module.destroy();
		}
		
		for(IPlugin plugin:plugins.values()){
			logger.info("stop plugin "+plugin.getClass().getName()+"...");
			plugin.stop();
		}
		
		txPlugin.stop();
		
		server.stop();
		logger.info("JOY Framework shutdown...");
	}
	
	private static void scanModules(){
		if(StringKit.isEmpty(server.getModulePackage()))
			return;
		int modulePackagePrefixLength = server.getModulePackage().length()+1;
		List<Class<? extends JoyModule>> moduleClassList = ClassKit.listClassBySuper(server.getModulePackage(), JoyModule.class);
		Collections.sort(moduleClassList, new Comparator<Class>() {
			public int compare(Class o1, Class o2) {
				return o1.getPackage().getName().compareTo(o2.getPackage().getName());
			};
		});
		for(Class moduleClass:moduleClassList){
			String moduleName = moduleClass.getPackage().getName().substring(modulePackagePrefixLength);
			moduleDefines.put(moduleName, JoyModule.create(moduleName, moduleClass));
			
			if(logger.isInfoEnabled())
				logger.info("found module: "+moduleName);
			modules.add(moduleName);
			
			String eventPackage = String.format(JoyManager.getServer().getEventPackagePattern(), moduleName);
			List<Class<? extends JoyEventListener>> listeners = ClassKit.listClassBySuper(eventPackage, JoyEventListener.class);
			for(Class listenerClass:listeners){
				if(logger.isInfoEnabled())
					logger.info("found module event listener: "+listenerClass);
				EventManager.addListener((JoyEventListener)BeanKit.getNewInstance(listenerClass));
			}
		}
	}
	
	public static IPlugin getPlugin(String pluginName){
		IPlugin plugin = plugins.get(pluginName); 
		if(plugin!=null && Boolean.parseBoolean(System.getProperty("plugin."+pluginName+".enable", "true")))
			return plugin;
		return null;
	}
	
	private static IPlugin loadPlugin(String pluginName) throws Exception{
		IPlugin plugin = plugins.get(pluginName);
		if(plugin==null){
			plugin = (IPlugin)BeanKit.getNewInstance("cn.joy.framework.plugin."+pluginName+"."
					+StringKit.capitalize(pluginName)+"Plugin");
			if(plugin==null)
				throw new RuntimeException("No plugin with name "+pluginName);
			plugins.put(pluginName, plugin);
		}
		return plugin; 
	}
	
	private static void loadOptionalPlugin(String pluginName){
		IPlugin plugin = plugins.get(pluginName);
		if(plugin==null){
			try {
				plugin = (IPlugin)BeanKit.getNewInstance("cn.joy.framework.plugin."+pluginName+"."
						+StringKit.capitalize(pluginName)+"Plugin");
				if(plugin!=null)
					plugins.put(pluginName, plugin);
			} catch (Exception e) {
				logger.info("No optional plugin with name "+pluginName);
			}
		}
	}
}
