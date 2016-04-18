package cn.joy.plugin.redis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Redis {
	private static Log log = LogKit.getLog(Redis.class);
	
	static Cache mainCache = null;
	
	static JedisPool mainPool = null;
	
	static String serializeWay = "kryo";
	
	private static final Map<String, JedisPool> poolMap = new ConcurrentHashMap<String, JedisPool>();
	private static final Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();
	
	static void addPool(String poolKey, JedisPool pool){
		if(mainPool==null)
			mainPool = pool;
		poolMap.put(poolKey, pool);
	}
	
	private static JedisPool usePool(String poolKey){
		if(poolKey==null)
			return mainPool;
		
		JedisPool pool = poolMap.get(poolKey);
		if(pool==null)
			return mainPool;
		return pool;
	}
	
	public static Cache use() {
		return mainCache;
	}
	
	public static Cache use(String cacheKey) {
		return use(null, cacheKey);
	}
	
	public static Cache use(String poolKey, String cacheKey) {
		Cache cache = cacheMap.get(cacheKey);
		if(cache==null){
			cache = Cache.create(usePool(poolKey), serializeWay);
			cacheMap.put(cacheKey, cache);
			if (mainCache == null)
				mainCache = cache;
		}
		return cache;
	}
	
	public static void unuse(String cacheName) {
		cacheMap.remove(cacheName);
	}
	
	public static Object call(JoyCallback callback) {
		return call(callback, use());
	}
	
	public static Object call(JoyCallback callback, String cacheName) {
		return call(callback, use(cacheName));
	}
	
	private static Object call(JoyCallback callback, Cache cache) {
		Jedis jedis = cache.getThreadJedis();
		boolean notThreadLocalJedis = (jedis == null);
		if (notThreadLocalJedis) {
			jedis = cache.jedisPool.getResource();
			cache.setThreadJedis(jedis);
		}
		try {
			return callback.run(cache).getContent();
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally {
			if (notThreadLocalJedis) {
				cache.removeThreadJedis();
				jedis.close();
			}
		}
	}
	
	public static void release(){
		cacheMap.clear();
		mainCache = null;
		
		for(Entry<String, JedisPool> entry:poolMap.entrySet()){
			try {
				entry.getValue().destroy();
			} catch (Exception e) {
				log.error("", e);
			}
		}
		mainPool = null;
	}
}




