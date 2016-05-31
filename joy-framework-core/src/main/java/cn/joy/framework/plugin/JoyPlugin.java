package cn.joy.framework.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.ClassKit;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.PathKit;
import cn.joy.framework.kits.PropKit;
import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.kits.StringKit;

public abstract class JoyPlugin {
	protected Log logger = LogKit.get();
	protected Prop config = null;
	protected boolean isStarted = false;
	
	public boolean init(){
		loadConfig();
		
		if(config==null){
			logger.warn("missing plugin["+this.getClass().getSimpleName()+"] properties");
			return false;
		}
		
		boolean isEnable = false;
		String enable = config.get("enable");
		if(enable.contains(".")){
			try {
				Class.forName(enable);
				isEnable = true;
			} catch (Exception e) {
			}
		}else{
			isEnable = StringKit.isTrue(enable);
		}
		
		if(isEnable){
			isStarted = start();
		}
		return isEnable;
	}
	
	public void release(){
		if(isStarted)
			stop();
	}
	
	public Prop getConfig() {
		return config;
	}
	
	protected void loadConfig(){
		try {
			String pluginKey = StringKit.trim(getClass().getSimpleName().toLowerCase(), "plugin");
			this.config = PropKit.empty();
			//先加载默认配置
			try {
				InputStream inputStream = ClassKit.getClassLoader().getResourceAsStream("plugins/"+pluginKey+"-default.properties");
				this.config.setAll(PropKit.use(inputStream));
			} catch (Exception e) {
			}
			
			String propFilePath = PathKit.getClassPath()+"/plugins/"+pluginKey+".properties";
			File propFile = new File(propFilePath);
			if(propFile.exists()){
				this.config.setAll(PropKit.use(propFile));
			}
			
			logger.debug("plugin config={}", this.config.getProperties());
			
			String envMode = JoyManager.getServer().getEnvMode();
			if(config!=null && !"product".equals(envMode)){
				envMode = envMode+"_";
				Properties prop = config.getProperties();
				for(String propName:prop.stringPropertyNames()){
					if(propName.startsWith(envMode)){
						prop.setProperty(propName.substring(envMode.length()), prop.getProperty(propName));
					}
				}
			}
		} catch (Exception e) {
			logger.warn("load config:"+e.getMessage());
		}
	}
	
	public abstract boolean start();

	public abstract void stop();
	
}
