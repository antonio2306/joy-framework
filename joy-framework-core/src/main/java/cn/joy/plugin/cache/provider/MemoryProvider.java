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
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import cn.joy.framework.kits.CollectionKit;
import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.provider.CacheProvider;

public class MemoryProvider<K, V> extends CacheProvider<K, V> {
	private LoadingCache<K, V> cache;
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Properties prop) { 
		CacheBuilder cacheBuilder = CacheBuilder.newBuilder();

		long expire = NumberKit.getLong(prop.get("expire"), 0L);
		if(expire>0)
			cacheBuilder.expireAfterWrite(expire, TimeUnit.SECONDS);
		if(expireCallback!=null)
			cacheBuilder.removalListener(new RemovalListener<K, V>() {
				@Override
				public void onRemoval(RemovalNotification<K, V> notification) {
					if(RemovalCause.EXPIRED.equals(notification.getCause())){
						try {
							expireCallback.run(notification.getKey(), notification.getValue());
						} catch (Exception e) {
							log.error("", e);
						}
					}
				}
			});
		cache = cacheBuilder.build(new CacheLoader<K, V>() {
					public V load(K key) throws Exception {
						if(missCallback!=null)
							return (V)missCallback.run(key);
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
	public void set(K key, V value) {
		cache.put(key, value);
	}
	
	@Override
	public V getSet(K key, V value) {
		V oldValue = this.get(key);
		this.set(key, value);
		return oldValue;
	}
	
	@Override
	public V get(K key){
		try {
			return cache.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void del(K key) {
		cache.invalidate(key);
	}

	@Override
	public void del(K... keys) {
		for(K key:keys){
			cache.invalidate(key);
		}
	}
	
	@Override
	public Set<String> keys(String pattern) {
		if("*".equals(pattern))
			return CollectionKit.convertSetType(cache.asMap().keySet(), String.class);
		Set<K> keys = cache.asMap().keySet();
		Set<String> returnKeys = new HashSet<>();
		for(K key:keys){
			String keyStr = key.toString();
			if(keyStr.matches(pattern))
				returnKeys.add(keyStr);
		}
		return returnKeys;
	}

	@Override
	public boolean exists(K key) {
		return cache.asMap().containsKey(key);
	}
	
	private Map getMap(K key){
		V value = get(key);
		if(value instanceof Map)
			return (Map)value;
		return null;
	}

	@Override
	public void hset(K key, Object field, Object value) {
		Map<Object, Object> map = (Map)getMap(key);
		if(map==null){
			map = new HashMap<>();
			cache.put(key, (V)map);
		}
		map.put(field, value);
	}

	@Override
	public Object hget(K key, Object field) {
		Map<Object, Object> map = (Map)getMap(key);
		if(map!=null)
			return map.get(field);
		return null;
	}

	@Override
	public void hdel(K key, Object... fields) {
		Map<Object, Object> map = (Map)getMap(key);
		if(map!=null){
			for(Object field:fields){
				map.remove(field);
			}
		}
	}

	@Override
	public boolean hexists(K key, Object field) {
		Map<Object, Object> map = (Map)getMap(key);
		if(map!=null)
			return map.containsKey(field);
		return false;
	}

	@Override
	public Map hgetAll(K key) {
		return (Map)getMap(key);
	}

	@Override
	public List<Object> hvals(K key) {
		Map<Object, Object> map = (Map)getMap(key);
		if(map!=null)
			return new ArrayList(map.values());
		return new ArrayList();
	}

	@Override
	public Set hkeys(K key) {
		Map<Object, Object> map = (Map)getMap(key);
		if(map!=null)
			return map.keySet();
		return new HashSet();
	}

}
