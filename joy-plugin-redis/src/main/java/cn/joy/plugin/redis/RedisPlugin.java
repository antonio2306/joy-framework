package cn.joy.plugin.redis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.Prop;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.ResourcePlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@Plugin(key="redis", depends = {"serialize"})
public class RedisPlugin extends ResourcePlugin<RedisResourceBuilder, RedisResource> {
	private final String defaultSerializeWay = "kryo";
	private String serializeWay;
	private JedisPool mainPool;
	private final Map<String, JedisPool> poolMap = new ConcurrentHashMap<String, JedisPool>();
	
	public static RedisResourceBuilder builder(){
		return new RedisResourceBuilder();
	}
	
	public static RedisPlugin plugin(){
		return (RedisPlugin)JoyManager.plugin("redis");
	}
	
	public boolean start() {
		Prop prop = getConfig();
		String[] pools = prop.get("pools").split(",");
		
		for(String poolKey:pools){
			String host = prop.get(poolKey+".host");
			int port = prop.getInt(poolKey+".port", Protocol.DEFAULT_PORT);
			int timeout = prop.getInt(poolKey+".timeout", Protocol.DEFAULT_TIMEOUT);
			String password = prop.get(poolKey+".password");
			int database = prop.getInt(poolKey+".database", Protocol.DEFAULT_DATABASE);
			String clientName = prop.get(poolKey+".clientName");
			
			JedisPoolConfig config = new JedisPoolConfig();
			if(prop.containsKey(poolKey+".pool.maxActive"))
				config.setMaxTotal(prop.getInt(poolKey+".pool.maxActive"));
			if(prop.containsKey(poolKey+".pool.maxIdle"))
				config.setMaxIdle(prop.getInt(poolKey+".pool.maxIdle"));
			if(prop.containsKey(poolKey+".pool.maxWait"))
				config.setMaxWaitMillis(prop.getInt(poolKey+".pool.maxWait"));
			if(prop.containsKey(poolKey+".pool.testOnBorrow"))
				config.setTestOnBorrow(prop.getBoolean(poolKey+".pool.testOnBorrow"));
			if(prop.containsKey(poolKey+".pool.testOnReturn"))
				config.setTestOnReturn(prop.getBoolean(poolKey+".pool.testOnReturn"));
			
			JedisPool jedisPool = null;
			if(StringKit.isEmpty(password))
				jedisPool = new JedisPool(config, host, port, timeout);
			else
				jedisPool = new JedisPool(config, host, port, timeout, password, database, clientName);
			
			poolMap.put(poolKey, jedisPool);
			if(mainPool==null)
				mainPool = jedisPool;
		}
		
		this.serializeWay = prop.get("serialize.way", defaultSerializeWay);
		this.mainResource = builder().pool(mainPool).serializeWay(serializeWay).build();
		
		return true;
	}
	
	public void stop() {
		for(Entry<String, JedisPool> entry:poolMap.entrySet()){
			try {
				entry.getValue().destroy();
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}
	
	public static RedisResource use() {
		return plugin().useResource();
	}
	
	public static RedisResource use(String name) {
		return plugin().useResource(name);
	}
	
	public static void unuse(String name) {
		plugin().unuseResource(name);
	}
	
	public JedisPool usePool(String poolKey){
		if(poolKey==null)
			return mainPool;
		
		JedisPool pool = poolMap.get(poolKey);
		if(pool==null)
			return mainPool;
		return pool;
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


