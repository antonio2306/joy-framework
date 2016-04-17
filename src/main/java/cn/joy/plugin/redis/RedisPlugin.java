package cn.joy.plugin.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.kits.Prop;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.JoyPlugin;

@Plugin(key="redis", depends = {"serialize"})
public class RedisPlugin extends JoyPlugin {
	public void start() {
		Prop prop = getConfig();
		String[] pools = prop.get("pools").split(",");
		
		for(String pool:pools){
			String host = prop.get(pool+".host");
			int port = prop.getInt(pool+".port", Protocol.DEFAULT_PORT);
			int timeout = prop.getInt(pool+".timeout", Protocol.DEFAULT_TIMEOUT);
			String password = prop.get(pool+".password");
			int database = prop.getInt(pool+".database", Protocol.DEFAULT_DATABASE);
			String clientName = prop.get(pool+".clientName");
			
			JedisPoolConfig config = new JedisPoolConfig();
			if(prop.containsKey(pool+".pool.maxActive"))
				config.setMaxTotal(prop.getInt(pool+".pool.maxActive"));
			if(prop.containsKey(pool+".pool.maxIdle"))
				config.setMaxIdle(prop.getInt(pool+".pool.maxIdle"));
			if(prop.containsKey(pool+".pool.maxWait"))
				config.setMaxWaitMillis(prop.getInt(pool+".pool.maxWait"));
			if(prop.containsKey(pool+".pool.testOnBorrow"))
				config.setTestOnBorrow(prop.getBoolean(pool+".pool.testOnBorrow"));
			if(prop.containsKey(pool+".pool.testOnReturn"))
				config.setTestOnReturn(prop.getBoolean(pool+".pool.testOnReturn"));
			
			JedisPool jedisPool = null;
			if(StringKit.isEmpty(password))
				jedisPool = new JedisPool(config, host, port, timeout);
			else
				jedisPool = new JedisPool(config, host, port, timeout, password, database, clientName);
			
			Redis.addPool(pool, jedisPool);
		}
		
		if(Redis.mainPool!=null){
			Redis.mainCache = Cache.create(Redis.mainPool);
		}
		
	}
	
	public void stop() {
		Redis.release();
	}
	
	public static void main(String[] args) {
		Jedis jedis = new Jedis("211.95.45.110", 46379);  
		jedis.select(2);
		String keys = "name";  
		  
		// 删数据  
		jedis.del(keys);  
		// 存数据  
		jedis.set(keys, "111");  
		// 取数据  
		String value = jedis.get(keys);  
		  
		System.out.println(value);  
		
		jedis.incr(keys);
		value = jedis.get(keys);  
		System.out.println(value);  
		
		System.out.println(jedis.getDB());
	}
}


