package cn.joy.plugin.cache;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public interface CacheProvider {
	void init(Properties prop);
	void release();
	
	void set(Object key, Object value);
	<T> T get(Object key);
	void del(Object key);
	void del(Object... keys);
	Set<Object> keys(String pattern);
	boolean exists(Object key);
	<T> T getSet(Object key, Object value);
	
	void hset(Object key, Object field, Object value);
	<T> T hget(Object key, Object field);
	void hdel(Object key, Object... fields);
	boolean hexists(Object key, Object field);
	Map hgetAll(Object key);
	List hvals(Object key);
	Set<Object> hkeys(Object key);
}
