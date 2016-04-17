package cn.joy.framework.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.StringKit;

public abstract class CacheProvider implements JoyProvider{
	private static final ConcurrentHashMap<String, CacheProvider> cacheMap = new ConcurrentHashMap<String, CacheProvider>();
	
	private static CacheProvider use(String key){
		return (CacheProvider)JoyManager.provider(CacheProvider.class, key);
	}
	
	public static CacheProvider build(String cacheName){
		return build(null, cacheName, null);
	}
	
	public static CacheProvider build(String cacheName, Properties prop){
		return build(null, cacheName, prop);
	}
	
	public static CacheProvider build(String key, String cacheName, Properties prop){
		CacheProvider cache = cacheMap.get(cacheName);
		if(cache==null){
			cache = (CacheProvider)BeanKit.getNewInstance(use(key).getClass());
			if(prop==null)
				prop = new Properties();
			prop.put("cacheName", cacheName);
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
	
	public abstract void init(Properties prop);
	public abstract void release();
	
	public abstract void set(Object key, Object value);
	public abstract <T> T get(Object key);
	public abstract void del(Object key);
	public abstract void del(Object... keys);
	public abstract Set<String> keys(String pattern);
	public abstract boolean exists(Object key);
	public abstract <T> T getSet(Object key, Object value);
	
	public abstract void hset(Object key, Object field, Object value);
	public abstract <T> T hget(Object key, Object field);
	public abstract void hdel(Object key, Object... fields);
	public abstract boolean hexists(Object key, Object field);
	public abstract Map hgetAll(Object key);
	public abstract List hvals(Object key);
	public abstract Set<Object> hkeys(Object key);
}
