package cn.joy.plugin.cache;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.kits.LogKit.Log;

public class Caches {
	private static Log log = LogKit.getLog(Caches.class);
	static String DEFAULT_PROVIDER = "memory";
	static CacheProvider mainCache;
	private static final ConcurrentHashMap<String, CacheProvider> cacheMap = new ConcurrentHashMap<String, CacheProvider>();
	
	public static CacheProvider use(String cacheName){
		return use(cacheName, null);
	}
	
	public static CacheProvider use(String cacheName, Properties prop){
		if(StringKit.isEmpty(cacheName))
			return mainCache;
		CacheProvider cache = cacheMap.get(cacheName);
		if(cache==null){
			String provider = StringKit.getString(prop==null?"":prop.getProperty("provider"), DEFAULT_PROVIDER);
			cache = (CacheProvider)BeanKit.getNewInstance(Caches.class.getPackage().getName()+".provider."+StringKit.capitalize(provider)+"Cache");
			cache.init(prop);
			cacheMap.put(cacheName, cache);
		}
		return cache;
	}
	
	public static void unuse(String cacheName){
		if(StringKit.isEmpty(cacheName))
			return;
		CacheProvider cache = cacheMap.get(cacheName);
		if(cache!=null){
			cache.release();
		}
		cacheMap.remove(cacheName);
	}
}
