package cn.joy.framework.plugin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.StringKit;

public abstract class PluginResourceManager<B extends PluginResourceBuilder<R>, R extends PluginResource> {
	protected Log log = LogKit.getLog(PluginResourceManager.class);
	protected R mainResource = null;
	protected final ConcurrentHashMap<String, R> resourceMap = new ConcurrentHashMap<String, R>();
	
	//PluginResourceManager(){}
	
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
	

	public void release(){
		mainResource.release();
		mainResource = null;
		
		for(Entry<String, R> entry:resourceMap.entrySet()){
			entry.getValue().release();
		}
		resourceMap.clear();
	}
}
