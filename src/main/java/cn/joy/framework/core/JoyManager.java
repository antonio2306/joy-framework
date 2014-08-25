package cn.joy.framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.joy.framework.event.EventManager;
import cn.joy.framework.event.JoyEventListener;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.ClassKit;
import cn.joy.framework.kits.PathKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.IMVCPlugin;
import cn.joy.framework.plugin.IPlugin;
import cn.joy.framework.plugin.IRoutePlugin;
import cn.joy.framework.plugin.ITransactionPlugin;
import cn.joy.framework.rule.RuleLoader;
import cn.joy.framework.server.AppServer;
import cn.joy.framework.server.CenterServer;
import cn.joy.framework.server.JoyServer;

/**
 * 框架管理器，负责启动时的初始化工作
 * @author liyy
 * @date 2014-05-20
 */
public class JoyManager {
	private static Logger logger = Logger.getLogger(JoyManager.class);
	private static Map<String, IPlugin> plugins = new HashMap();
	private static List<String> modules = new ArrayList();
	
	private static RuleLoader rLoader;
	private static JoyServer server;
	private static IMVCPlugin mvcPlugin;
	private static ITransactionPlugin txPlugin;
	private static IRoutePlugin routePlugin;
	
	public static RuleLoader getRuleLoader() {
		return rLoader;
	}
	
	public static IMVCPlugin getMVCPlugin() {
		return mvcPlugin;
	}
	
	public static ITransactionPlugin getTransactionPlugin() {
		return txPlugin;
	}
	
	public static IRoutePlugin getRoutePlugin() {
		return routePlugin;
	}
	
	public static JoyServer getServer(){
		return server;
	}

	public static void init() throws Exception{
		logger.info("JOY Framework start...");
		
		Properties config = new Properties();
		
		boolean isCenterServer = true;
		File configFile = new File(PathKit.getClassPath()+"/joy-center.cnf");
		
		if(!configFile.exists()){
			configFile = new File(PathKit.getClassPath()+"/joy-app.cnf");
			isCenterServer = false;
		}
		
		if(!configFile.exists())
			throw new RuntimeException("No JOY config file exists!");
		
		config.load(new FileInputStream(configFile));
		server = isCenterServer?new CenterServer():new AppServer();
		server.init(config);
		
		mvcPlugin = (IMVCPlugin)loadPlugin(server.getMVCPlugin());
		
		txPlugin = (ITransactionPlugin)loadPlugin(server.getTransactionPlugin());
		
		routePlugin = (IRoutePlugin)loadPlugin(server.getRoutePlugin());
		
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
		
		logger.info("JOY Framework run...");
	}
	
	private static IPlugin loadPlugin(String pluginName) throws Exception{
		IPlugin plugin = plugins.get(pluginName);
		if(plugin==null){
			plugin = (IPlugin)BeanKit.getNewInstance("cn.joy.framework.plugin."+pluginName+"."
					+StringKit.capitalize(pluginName)+"Plugin");
			plugins.put(pluginName, plugin);
		}
		return plugin; 
	}
}
