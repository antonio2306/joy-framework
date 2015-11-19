package cn.joy.framework.core;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import cn.joy.framework.annotation.Module;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.Prop;
import cn.joy.framework.kits.PropKit;
import cn.joy.framework.kits.StringKit;

public class JoyModule {
	private static Logger logger = Logger.getLogger(JoyModule.class);
	
	private String name;

	private String description;

	private Class moduleDefineClass;
	
	private Object moduleObj;

	private String moduleInitMethod;
	
	private Prop moduleConfig = null;
	
	public String getModuleProperty(String key){
		if(moduleConfig!=null)
			return moduleConfig.get(key);
		return null;
	}
	
	public static JoyModule create(Class moduleDefineClass){
		Module moduleAnnotation = (Module) moduleDefineClass.getAnnotation(Module.class);
		if(moduleAnnotation==null)
			throw new RuntimeException("Wrong Module Object");
		
		JoyModule joyModule = new JoyModule();
		joyModule.moduleDefineClass = moduleDefineClass;
		joyModule.moduleObj = BeanKit.getNewInstance(moduleDefineClass);
		joyModule.name = moduleAnnotation.name();
		joyModule.description = moduleAnnotation.desc();
		joyModule.moduleInitMethod = moduleAnnotation.init();
		
		joyModule.init();
		
		return joyModule;
	}

	public void init() {
		try {
			this.moduleConfig = PropKit.use(moduleDefineClass.getPackage().getName().replaceAll("\\.", "/")+"/"+moduleDefineClass.getSimpleName().toLowerCase()
					.replace("module", "")+".properties");
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		
		if (moduleObj != null && StringKit.isNotEmpty(moduleInitMethod)) {
			try {
				Method initMethod = moduleDefineClass.getDeclaredMethod(moduleInitMethod);
				if(initMethod!=null)
					initMethod.invoke(moduleObj);
			} catch (Exception e) {
				logger.error("", e);
			}
		}

	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
