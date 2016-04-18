package cn.joy.plugin.redis.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.provider.CacheProvider;
import cn.joy.plugin.redis.Cache;
import cn.joy.plugin.redis.Redis;

public class RedisProvider<K, V> extends CacheProvider<K, V> {
	private Cache cache;
	private int expire;
	private String cacheName;

	@Override
	public void init(Properties prop) {
		this.cacheName = prop.getProperty("cacheName");
		cache = Redis.use(cacheName);
		expire = NumberKit.getInteger(prop.get("expire"), 0);
	}

	@Override
	public void release() {
		cache.release();
		cache = null;
	}
	
	private Object getRealKey(K key){
		if(StringKit.isNotEmpty(cacheName))
			return cacheName+"."+key;
		return key;
	}

	@Override
	public void set(K key, V value) {
		if (expire > 0)
			cache.setex(getRealKey(key), expire, value);
		else
			cache.set(getRealKey(key), value);
	}

	@Override
	public V get(K key) {
		V value = cache.get(getRealKey(key));
		if(value==null && cacheLoader!=null){
			try {
				value = cacheLoader.load(key);
				set(key, value);
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return value;
	}

	@Override
	public void del(K key) {
		cache.del(getRealKey(key));
	}

	@Override
	public void del(K... keys) {
		for(K key:keys){
			cache.del(getRealKey(key));
		}
	}
	
	@Override
	public Set<String> keys(String pattern) {
		return cache.keys(pattern);
	}

	@Override
	public boolean exists(K key) {
		return cache.exists(getRealKey(key));
	}

	@Override
	public V getSet(K key, V value) {
		V v = cache.getSet(getRealKey(key), value);
		if (expire > 0)
			cache.expire(getRealKey(key), expire);
		return v;
	}

	@Override
	public void hset(K key, Object field, Object value) {
		cache.hset(getRealKey(key), field, value);
		if (expire > 0)
			cache.expire(getRealKey(key), expire);
	}

	@Override
	public Object hget(K key, Object field) {
		return cache.hget(getRealKey(key), field);
	}

	@Override
	public void hdel(K key, Object... fields) {
		cache.hdel(getRealKey(key), fields);
	}

	@Override
	public boolean hexists(K key, Object field) {
		return cache.hexists(getRealKey(key), field);
	}

	@Override
	public Map hgetAll(K key) {
		return cache.hgetAll(getRealKey(key));
	}

	@Override
	public List hvals(K key) {
		return cache.hvals(getRealKey(key));
	}

	@Override
	public Set hkeys(K key) {
		return cache.hkeys(getRealKey(key));
	}

}
