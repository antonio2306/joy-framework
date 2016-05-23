package cn.joy.plugin.redis.provider;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.joy.framework.kits.StringKit;
import cn.joy.framework.kits.TypeKit;
import cn.joy.framework.provider.CacheProvider;
import cn.joy.plugin.redis.Redis;
import cn.joy.plugin.redis.RedisResource;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisProvider<K, V> extends CacheProvider<K, V> {
	private RedisResource cache;
	private int expire;
	private String cacheName;
	private Jedis expireCallbackJedis;

	@Override
	public void init(Properties prop) {
		this.cacheName = prop.getProperty("cacheName");
		cache = Redis.use(cacheName);
		expire = TypeKit.toInt(prop.get("expire"), 0);
		if (expireCallback != null) {
			expireCallbackJedis = cache.getPool().getResource();
			new Thread(new Runnable() {
				@Override
				public void run() {
					expireCallbackJedis.psubscribe(new JedisPubSub() {
					    // 初始化按表达式的方式订阅时候的处理  
					    public void onPSubscribe(String pattern, int subscribedChannels) {  
					        //System.out.println(pattern + "=" + subscribedChannels);  
					    }  
					  
					    // 取得按表达式的方式订阅的消息后的处理  
					    public void onPMessage(String pattern, String channel, String message) {
					    	if(channel.endsWith("expired")){
					    		if (StringKit.isNotEmpty(cacheName)){
					    			if(!message.startsWith(getKeyPrefix()))
					    				return;
					    			message = message.substring(getKeyPrefix().length());
					    		}
					    		try {
									expireCallback.run(message, null);
								} catch (Exception e) {
									log.error("", e);
								}
					    	}
					    }  
					}, "*expired");
				}
			}).start();
		}
	}

	@Override
	public void release() {
		cache.release();
		cache = null;
		
		expireCallbackJedis.close();
		expireCallbackJedis = null;
	}
	
	private String getKeyPrefix(){
		if (StringKit.isNotEmpty(cacheName))
			return cacheName + ".";
		return "";
	}

	private Object getRealKey(K key) {
		if (StringKit.isNotEmpty(cacheName))
			return getKeyPrefix() + key;
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
		if (value == null && missCallback != null) {
			try {
				value = (V)missCallback.run(key);
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
		for (K key : keys) {
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
