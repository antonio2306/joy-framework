package cn.joy.framework.plugin;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.ClassKit;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.PathKit;
import cn.joy.framework.kits.Prop;
import cn.joy.framework.kits.PropKit;
import cn.joy.framework.kits.StringKit;

public abstract class JoyPlugin<B extends PluginResourceBuilder<R>, R extends PluginResource> {
	protected static Log log = LogKit.getLog(JoyPlugin.class);
	private Prop config = null;
	
	protected R mainResource = null;
	protected final ConcurrentHashMap<String, R> resourceMap = new ConcurrentHashMap<String, R>();
	
	public boolean init(){
		loadConfig();
		
		if(config==null){
			log.warn("missing plugin["+this.getClass().getSimpleName()+"] properties");
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
		
		if(isEnable)
			start();
		return isEnable;
	}
	
	public void release(){
		mainResource.release();
		mainResource = null;
		
		for(Entry<String, R> entry:resourceMap.entrySet()){
			entry.getValue().release();
		}
		resourceMap.clear();
		
		stop();
	}
	
	public Prop getConfig() {
		return config;
	}
	
	protected void loadConfig(){
		try {
			String pluginKey = StringKit.trim(getClass().getSimpleName().toLowerCase(), "plugin");
			String propFilePath = PathKit.getClassPath()+"/plugins/"+pluginKey+".properties";
			File propFile = new File(propFilePath);
			if(propFile.exists()){
				this.config = PropKit.use(propFile);
			}else{
				InputStream inputStream = ClassKit.getClassLoader().getResourceAsStream("plugins/"+pluginKey+"-default.properties");
				this.config = PropKit.use(inputStream);
			}
			
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
			log.warn(e.getMessage());
		}
	}
	
	public void initMainResource(R mainResource){
		this.mainResource = mainResource;
	}
	
	public R use(){
		return mainResource;
	}
	
	public R use(String name){
		if(StringKit.isEmpty(name))
			return mainResource;
		R resource = resourceMap.get(name);
		if(resource==null){
			Type genType = this.getClass().getGenericSuperclass();
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			Class<B> builderClass = (Class) params[0] ;
			try {
				B builder = (B)builderClass.newInstance();
				resource = builder.build();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			resourceMap.put(name, resource);
		}
		return resource;
	}
	
	public void unuse(String name){
		if(StringKit.isEmpty(name))
			return;
		R resource = resourceMap.get(name);
		if(resource!=null){
			resource.release();
		}
		resourceMap.remove(name);
	}

	public abstract void start();

	public abstract void stop();
	
}
