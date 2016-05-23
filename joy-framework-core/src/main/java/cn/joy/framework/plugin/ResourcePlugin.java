package cn.joy.framework.plugin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.kits.StringKit;

public abstract class ResourcePlugin<B extends PluginResourceBuilder<R>, R extends PluginResource> extends JoyPlugin{
	protected R mainResource = null;
	protected final ConcurrentHashMap<String, R> resourceMap = new ConcurrentHashMap<String, R>();
	
	public void release(){
		mainResource.release();
		mainResource = null;
		
		for(Entry<String, R> entry:resourceMap.entrySet()){
			entry.getValue().release();
		}
		resourceMap.clear();
		
		super.release();
	}
	
	public R useResource(){
		return mainResource;
	}
	
	public R useResource(String name){
		if(StringKit.isEmpty(name))
			return mainResource;
		R resource = resourceMap.get(name);
		if(resource==null){
			Type genType = this.getClass().getGenericSuperclass();
			Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
			Class<B> builderClass = (Class) params[0] ;
			try {
				B builder = (B)builderClass.newInstance();
				resource = builder.name(name).build();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			resourceMap.put(name, resource);
		}
		return resource;
	}
	
	public R useResource(String name, JoyCallback missCallback){
		if(StringKit.isEmpty(name))
			return mainResource;
		R resource = resourceMap.get(name);
		if(resource==null && missCallback!=null){
			try {
				resource = (R)missCallback.run(name);
				resourceMap.put(name, resource);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return resource;
	}
	
	public R useResource(String name, R resource){
		return resourceMap.put(name, resource);
	}
	
	public void unuseResource(String name){
		if(StringKit.isEmpty(name))
			return;
		R resource = resourceMap.get(name);
		if(resource!=null){
			resource.release();
		}
		resourceMap.remove(name);
	}
	
	public R cacheResource(String name, R resource){
		resourceMap.put(name, resource);
		return resource;
	}

}
