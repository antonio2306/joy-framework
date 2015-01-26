package cn.joy.framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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
	private static RouteStore routeStore;
	
	public static SecurityManager getSecurityManager() {
		if(securityManager==null)
			securityManager = new DefaultSecurityManager();
		return securityManager;
	}

	public static void setSecurityManager(SecurityManager securityManager) {
		JoyManager.securityManager = securityManager;
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
		return server;
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
		
		if(!configFile.exists())
			throw new RuntimeException("No JOY config file exists!");
		
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
		
		File moduleBaseDir = new File(PathKit.getPackagePath(server.getModulePackage()));
		logger.info("module base dir: "+moduleBaseDir);
		if(moduleBaseDir.exists()){
			File[] moduleDirs = moduleBaseDir.listFiles();
			for(File moduleDir:moduleDirs){
				if(moduleDir.isDirectory()){
					String moduleName = moduleDir.getName();
					if(logger.isInfoEnabled())
						logger.info("found module: "+moduleName);
					modules.add(moduleName);
					
					List moduleClasses = ClassKit.getClasses(server.getModulePackage()+"."+moduleName, false);
					if(moduleClasses!=null){
						for (Object md : moduleClasses) {
							Class mdClass = (Class) md;
							if(mdClass.getAnnotation(Module.class)==null)
								continue;
							moduleDefines.put(moduleName, JoyModule.create(mdClass));
							if(logger.isInfoEnabled())
								logger.info("create joy module: "+moduleName);
						}
					}
					
					String eventPackage = String.format(JoyManager.getServer().getEventPackagePattern(), moduleName);
					List<Class> listeners = ClassKit.getAllClassByInterface(eventPackage, JoyEventListener.class);
					for(Class listenerClass:listeners){
						if(logger.isInfoEnabled())
							logger.info("found module event listener: "+listenerClass);
						EventManager.addListener((JoyEventListener)BeanKit.getNewInstance(listenerClass));
					}
				}
			}
		}
		
		getRouteStore().initRoute();
		logger.info("JOY Framework run...");
	}
	
	public static IPlugin getPlugin(String pluginName){
		return plugins.get(pluginName); 
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
