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
		
		File moduleBaseDir = new File(PathKit.getPackagePath(server.getModulePackage()));
		logger.info("module base dir: "+moduleBaseDir);
		if(moduleBaseDir.exists()){
			scanModules(moduleBaseDir, "");
		}
		
		getRouteStore().initRoute();
		logger.info("JOY Framework run...");
	}
	
	private static void scanModules(File moduleBaseDir, String parentPath){
		File[] moduleDirs = moduleBaseDir.listFiles();
		for(File moduleDir:moduleDirs){
			if(moduleDir.isDirectory()){
				boolean isModule = false;
				boolean hasSubs = false;
				String moduleName = parentPath+moduleDir.getName();
				List moduleClasses = ClassKit.getClasses(server.getModulePackage()+"."+moduleName, false);
				if(moduleClasses!=null && moduleClasses.size()>0){
					for (Object md : moduleClasses) {
						Class mdClass = (Class) md;
						Module moduleAnnotation = (Module) mdClass.getAnnotation(Module.class);
						if(moduleAnnotation==null)
							continue;
						moduleDefines.put(moduleName, JoyModule.create(moduleName, mdClass));
						if(logger.isInfoEnabled())
							logger.info("create joy module: "+moduleName);
						isModule = true;
						hasSubs = moduleAnnotation.hasSubs();
						break;
					}
				}

				if(!isModule){
					scanModules(moduleDir, moduleName+".");
					continue;
				}
				
				if(logger.isInfoEnabled())
					logger.info("found module: "+moduleName);
				modules.add(moduleName);
				
				String eventPackage = String.format(JoyManager.getServer().getEventPackagePattern(), moduleName);
				List<Class> listeners = ClassKit.getAllClassByInterface(eventPackage, JoyEventListener.class);
				for(Class listenerClass:listeners){
					if(logger.isInfoEnabled())
						logger.info("found module event listener: "+listenerClass);
					EventManager.addListener((JoyEventListener)BeanKit.getNewInstance(listenerClass));
				}
				
				if(hasSubs){
					scanModules(moduleDir, moduleName+".");
				}
			}
		}
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
