package cn.joy.plugin.redis.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.provider.CacheProvider;
import cn.joy.plugin.redis.Cache;
import cn.joy.plugin.redis.Redis;

public class RedisProvider extends CacheProvider {
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
	public void set(Object key, Object value) {
		if (expire > 0)
			cache.setex(key, expire, value);
		else
			cache.set(key, value);
	}

	@Override
	public <T> T get(Object key) {
		return cache.get(key);
	}

	@Override
	public void del(Object key) {
		cache.del(key);
	}

	@Override
	public void del(Object... keys) {
		cache.del(keys);
	}

	@Override
	public Set<String> keys(String pattern) {
		return cache.keys(pattern);
	}

	@Override
	public boolean exists(Object key) {
		return cache.exists(key);
	}

	@Override
	public <T> T getSet(Object key, Object value) {
		T t = cache.getSet(key, value);
		if (expire > 0)
			cache.expire(key, expire);
		return t;
	}

	@Override
	public void hset(Object key, Object field, Object value) {
		cache.hset(key, field, value);
		if (expire > 0)
			cache.expire(key, expire);
	}

	@Override
	public <T> T hget(Object key, Object field) {
		return cache.hget(key, field);
	}

	@Override
	public void hdel(Object key, Object... fields) {
		cache.hdel(key, fields);
	}

	@Override
	public boolean hexists(Object key, Object field) {
		return cache.hexists(key, field);
	}

	@Override
	public Map hgetAll(Object key) {
		return cache.hgetAll(key);
	}

	@Override
	public List hvals(Object key) {
		return cache.hvals(key);
	}

	@Override
	public Set<Object> hkeys(Object key) {
		return cache.hkeys(key);
	}

}
