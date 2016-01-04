package cn.joy.framework.core;

import java.util.Properties;

import org.apache.log4j.Logger;

import cn.joy.framework.annotation.Module;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.Prop;
import cn.joy.framework.kits.PropKit;

public abstract class JoyModule {
	private static Logger logger = Logger.getLogger(JoyModule.class);
	
	private String key;
	
	private String name;
	
	private String code;

	private String description;

	private Prop moduleConfig = null;
	
	public String getModuleProperty(String key){
		if(moduleConfig!=null)
			return moduleConfig.get(key);
		return null;
	}
	
	public static JoyModule create(String moduleKey, Class moduleDefineClass){
		Module moduleAnnotation = (Module) moduleDefineClass.getAnnotation(Module.class);
		if(moduleAnnotation==null)
			throw new RuntimeException("Wrong Module Object");
		
		JoyModule joyModule = BeanKit.getNewInstance(moduleDefineClass);//new JoyModule();
		joyModule.key = moduleKey;
		joyModule.name = moduleAnnotation.name();
		joyModule.code = moduleAnnotation.code();
		joyModule.description = moduleAnnotation.desc();
		
		joyModule.init();
		
		return joyModule;
	}

	public void init() {
		try {
			String propFile = getClass().getSimpleName().toLowerCase().replace("module", "")+".properties";
			this.moduleConfig = PropKit.use(getClass().getPackage().getName().replaceAll("\\.", "/")+"/"+propFile);
			String envMode = JoyManager.getServer().getEnvMode();
			if(moduleConfig!=null && !"product".equals(envMode)){
				envMode = envMode+"_";
				Properties prop = moduleConfig.getProperties();
				for(String propName:prop.stringPropertyNames()){
					if(propName.startsWith(envMode)){
						prop.setProperty(propName.substring(envMode.length()), prop.getProperty(propName));
					}
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		
		if(!"false".equals(getModuleProperty("enable")))
			initModule();
	}
	
	public void destroy() {
		if(!"false".equals(getModuleProperty("enable")))
			destroyModule();
	}
	
	public abstract void initModule();
	
	public void destroyModule(){
		
	}
	
	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
