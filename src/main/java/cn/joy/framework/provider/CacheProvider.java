package cn.joy.framework.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.StringKit;

public abstract class CacheProvider<K, V> implements JoyProvider{
	protected Log log = LogKit.getLog(CacheProvider.class);
	private static final Map<String, CacheProvider> cacheMap = new ConcurrentHashMap<>();
	protected Loader<K, V> cacheLoader;
	
	private static CacheProvider way(String way){
		return (CacheProvider)JoyManager.provider(CacheProvider.class, way);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName){
		return use(null, cacheName, null, null);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName){
		return use(way, cacheName, null, null);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName, Loader<K, V> cacheLoader){
		return use(null, cacheName, null, cacheLoader);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName, Loader<K, V> cacheLoader){
		return use(way, cacheName, null, cacheLoader);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName, Properties prop){
		return use(null, cacheName, prop, null);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName, Properties prop){
		return use(way, cacheName, prop, null);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName, Properties prop, Loader<K, V> cacheLoader){
		return use(null, cacheName, prop, cacheLoader);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName, Properties prop, Loader<K, V> cacheLoader){
		CacheProvider cache = cacheMap.get(cacheName);
		if(cache==null){
			cache = (CacheProvider)BeanKit.getNewInstance(way(way).getClass());
			if(prop==null)
				prop = new Properties();
			prop.put("cacheName", cacheName);
			cache.setCacheLoader(cacheLoader).init(prop);
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
	
	protected CacheProvider setCacheLoader(Loader<K, V> cacheLoader){
		this.cacheLoader = cacheLoader;
		return this;
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
	
	public static abstract class Loader<K, V>{
		public abstract V load(K key) throws Exception;
	}
}
