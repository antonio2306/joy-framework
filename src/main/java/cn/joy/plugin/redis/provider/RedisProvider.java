package cn.joy.plugin.redis.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.provider.CacheProvider;
import cn.joy.plugin.redis.Cache;
import cn.joy.plugin.redis.Redis;

public class RedisProvider<K, V> extends CacheProvider<K, V> {
	private Cache cache;
	private int expire;

	@Override
	public void init(Properties prop) {
		cache = Redis.use(prop.getProperty("cacheName"));
		expire = NumberKit.getInteger(prop.get("expire"), 0);
	}

	@Override
	public void release() {
		cache.release();
		cache = null;
	}

	@Override
	public void set(K key, V value) {
		if (expire > 0)
			cache.setex(key, expire, value);
		else
			cache.set(key, value);
	}

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public void del(K key) {
		cache.del(key);
	}

	@Override
	public void del(K... keys) {
		cache.del(keys);
	}
	
	@Override
	public Set<String> keys(String pattern) {
		return cache.keys(pattern);
	}

	@Override
	public boolean exists(K key) {
		return cache.exists(key);
	}

	@Override
	public V getSet(K key, V value) {
		V v = cache.getSet(key, value);
		if (expire > 0)
			cache.expire(key, expire);
		return v;
	}

	@Override
	public void hset(K key, Object field, Object value) {
		cache.hset(key, field, value);
		if (expire > 0)
			cache.expire(key, expire);
	}

	@Override
	public Object hget(K key, Object field) {
		return cache.hget(key, field);
	}

	@Override
	public void hdel(K key, Object... fields) {
		cache.hdel(key, fields);
	}

	@Override
	public boolean hexists(K key, Object field) {
		return cache.hexists(key, field);
	}

	@Override
	public Map hgetAll(K key) {
		return cache.hgetAll(key);
	}

	@Override
	public List hvals(K key) {
		return cache.hvals(key);
	}

	@Override
	public Set hkeys(K key) {
		return cache.hkeys(key);
	}

}
