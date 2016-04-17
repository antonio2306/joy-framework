package cn.joy.framework.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.StringKit;

public abstract class CacheProvider<K, V> implements JoyProvider{
	private static final Map<String, CacheProvider> cacheMap = new ConcurrentHashMap<>();
	
	private static CacheProvider use(String key){
		return (CacheProvider)JoyManager.provider(CacheProvider.class, key);
	}
	
	public static <K, V> CacheProvider<K, V> build(String cacheName){
		return build(null, cacheName, null);
	}
	
	public static <K, V> CacheProvider<K, V> build(String cacheName, Properties prop){
		return build(null, cacheName, prop);
	}
	
	public static <K, V> CacheProvider<K, V> build(String key, String cacheName, Properties prop){
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
	
	public abstract void set(K key, V value);
	public abstract V get(K key);
	public abstract void del(K key);
	public abstract void del(K... keys);
	public abstract Set<String> keys(String pattern);
	public abstract boolean exists(K key);
	public abstract V getSet(K key, V value);
	
	public abstract void hset(K key, Object field, Object value);
	public abstract Object hget(K key, Object field);
	public abstract void hdel(K key, Object... fields);
	public abstract boolean hexists(K key, Object field);
	public abstract Map hgetAll(K key);
	public abstract List hvals(K key);
	public abstract Set hkeys(K key);
}
