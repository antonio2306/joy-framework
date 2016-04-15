package cn.joy.plugin.cache.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import cn.joy.framework.kits.NumberKit;
import cn.joy.plugin.cache.CacheProvider;

public class MemoryCache implements CacheProvider {
	private LoadingCache<Object, Object> cache;
	
	@Override
	public void init(Properties prop) { 
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();

		long expire = prop==null?0:NumberKit.getLong(prop.get("expire"), 0L);
		if(expire>0)
			cacheBuilder.expireAfterWrite(expire, TimeUnit.SECONDS);
		cache = cacheBuilder.build(new CacheLoader<Object, Object>() {
					public Object load(Object key) throws Exception {
						return null;
					}
				});
	}

	@Override
	public void release() {
		if(cache!=null)
			cache.cleanUp();
		cache = null;
	}

	@Override
	public void set(Object key, Object value) {
		cache.put(key, value);
	}

	@Override
	public <T> T get(Object key){
		return (T)cache.getIfPresent(key);
	}

	@Override
	public void del(Object key) {
		cache.invalidate(key);
	}

	@Override
	public void del(Object... keys) {
		for(Object key:keys){
			cache.invalidate(key);
		}
	}

	@Override
	public Set<Object> keys(String pattern) {
		return cache.asMap().keySet();
	}

	@Override
	public boolean exists(Object key) {
		return cache.asMap().containsKey(key);
	}

	@Override
	public <T> T getSet(Object key, Object value) {
		Object oldValue = this.get(key);
		this.set(key, value);
		return (T)oldValue;
	}

	@Override
	public void hset(Object key, Object field, Object value) {
		Map<Object, Object> map = (Map)cache.getIfPresent(key);
		if(map==null){
			map = new HashMap<>();
			cache.put(key, map);
		}
		map.put(field, value);
	}

	@Override
	public <T> T hget(Object key, Object field) {
		Map<Object, Object> map = (Map)cache.getIfPresent(key);
		if(map!=null)
			return (T)map.get(field);
		return null;
	}

	@Override
	public void hdel(Object key, Object... fields) {
		Map<Object, Object> map = (Map)cache.getIfPresent(key);
		if(map!=null){
			for(Object field:fields){
				map.remove(field);
			}
		}
	}

	@Override
	public boolean hexists(Object key, Object field) {
		Map<Object, Object> map = (Map)cache.getIfPresent(key);
		if(map!=null)
			return map.containsKey(field);
		return false;
	}

	@Override
	public Map hgetAll(Object key) {
		return (Map)cache.getIfPresent(key);
	}

	@Override
	public List<Object> hvals(Object key) {
		Map<Object, Object> map = (Map)cache.getIfPresent(key);
		if(map!=null)
			return new ArrayList(map.values());
		return new ArrayList();
	}

	@Override
	public Set<Object> hkeys(Object key) {
		Map<Object, Object> map = (Map)cache.getIfPresent(key);
		if(map!=null)
			return map.keySet();
		return new HashSet();
	}

}
