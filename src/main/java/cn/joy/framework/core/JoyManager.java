package cn.joy.framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.PathKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.mvc.IMVCPlugin;
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
	
	private static RuleLoader rLoader;
	private static JoyServer server;
	private static IMVCPlugin mvcPlugin;
	
	public static RuleLoader getRuleLoader() {
		return rLoader;
	}
	
	public static IMVCPlugin getMVCPlugin() {
		return mvcPlugin;
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
		
		String mvcPluginName = server.getMVCPlugin();
		mvcPlugin = (IMVCPlugin)BeanKit.getNewInstance("cn.joy.framework.plugin.mvc."+mvcPluginName+"."
				+StringKit.capitalize(mvcPluginName)+"Plugin");
		mvcPlugin.start();
		
		rLoader = RuleLoader.singleton();
		
		logger.debug("RuleManager init end...");
	}
	
}
