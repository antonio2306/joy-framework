package cn.joy.framework.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.BeanKit;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.StringKit;

public abstract class CacheProvider<K, V> implements JoyProvider{
	protected Log log = LogKit.getLog(CacheProvider.class);
	private static final Map<String, CacheProvider> cacheMap = new ConcurrentHashMap<>();
	protected JoyCallback loaderCallback;
	protected JoyCallback expireCallback;
	
	private static CacheProvider way(String way){
		return (CacheProvider)JoyManager.provider(CacheProvider.class, way);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName){
		return use(cacheName, 0);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName, JoyCallback loaderCallback){
		return use(cacheName, loaderCallback, 0);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName, int expireTime){
		return use(cacheName, new JoyCallback() {
			@Override
			public Object run(Object... params) throws Exception {
				return null;
			}
		}, expireTime);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName, JoyCallback loaderCallback, int expireTime){
		return use(cacheName, loaderCallback, expireTime, null);
	}
	
	public static <K, V> CacheProvider<K, V> use(String cacheName, JoyCallback loaderCallback, int expireTime, JoyCallback expireCallback){
		return use(null, cacheName, null, loaderCallback, expireTime, expireCallback);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName){
		return use(way, cacheName, 0);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName, JoyCallback loaderCallback){
		return use(way, cacheName, loaderCallback, 0);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName, int expireTime){
		return use(way, cacheName, null, expireTime);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName, JoyCallback loaderCallback, int expireTime){
		return use(way, cacheName, loaderCallback, expireTime, null);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName, JoyCallback loaderCallback, int expireTime, JoyCallback expireCallback){
		return use(way, cacheName, null, loaderCallback, expireTime, expireCallback);
	}
	
	public static <K, V> CacheProvider<K, V> use(String way, String cacheName, Properties prop, JoyCallback loaderCallback, int expireTime, JoyCallback expireCallback){
		CacheProvider cache = cacheMap.get(cacheName);
		if(cache==null){
			cache = (CacheProvider)BeanKit.getNewInstance(way(way).getClass());
			if(prop==null)
				prop = new Properties();
			prop.put("cacheName", cacheName);
			prop.put("expire", expireTime);
			cache.setLoaderCallback(loaderCallback).setExpireCallback(expireCallback).init(prop);
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
	
	protected CacheProvider<K, V> setLoaderCallback(JoyCallback loaderCallback){
		this.loaderCallback = loaderCallback;
		return this;
	}
	
	protected CacheProvider<K, V> setExpireCallback(JoyCallback expireCallback){
		this.expireCallback = expireCallback;
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
	
}
