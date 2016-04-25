package cn.joy.plugin.redis;

import cn.joy.framework.plugin.PluginResourceBuilder;
import redis.clients.jedis.JedisPool;

public class RedisResourceBuilder extends PluginResourceBuilder<RedisResource> {
	private JedisPool pool;
	private String serializeWay;
	
	public RedisResourceBuilder pool(String poolKey){
		this.pool = Redis.plugin().usePool(poolKey);
		return this;
	}
	
	public RedisResourceBuilder serializeWay(String serializeWay){
		this.serializeWay = serializeWay;
		return this;
	}
	
	public RedisResourceBuilder pool(JedisPool pool){
		this.pool = pool;
		return this;
	}
	
	@Override
	public RedisResource build() {
		RedisResource resource = new RedisResource(pool, serializeWay);
		return resource;
	}
}
