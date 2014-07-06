package cn.joy.framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.PathKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.IMVCPlugin;
import cn.joy.framework.plugin.IPlugin;
import cn.joy.framework.plugin.ITransactionPlugin;
import cn.joy.framework.rule.RuleLoader;
import cn.joy.framework.server.AppServer;
import cn.joy.framework.server.CenterServer;
import cn.joy.framework.server.JoyServer;

/**
 * 业务规则管理器
 * @author liyy
 * @date 2014-05-20
 */
public class JoyManager {
	private static Logger logger = Logger.getLogger(JoyManager.class);
	private static Map<String, IPlugin> plugins = new HashMap();
	
	private static RuleLoader rLoader;
	private static JoyServer server;
	private static IMVCPlugin mvcPlugin;
	private static ITransactionPlugin txPlugin;
	
	public static RuleLoader getRuleLoader() {
		return rLoader;
	}
	
	public static IMVCPlugin getMVCPlugin() {
		return mvcPlugin;
	}
	
	public static ITransactionPlugin getTransactionPlugin() {
		return txPlugin;
	}
	
	public static JoyServer getServer(){
		return server;
	}

	public static void init() throws Exception{
		logger.debug("RuleManager init start...");
		
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
		
		for(IPlugin plugin:plugins.values()){
			plugin.start();
		}
		
		rLoader = RuleLoader.singleton();
		
		logger.debug("RuleManager init end...");
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