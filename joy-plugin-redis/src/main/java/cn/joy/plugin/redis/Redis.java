package cn.joy.plugin.redis;

public class Redis extends RedisPlugin{
	
	/*public static Object call(JoyCallback callback) {
		return call(callback, use());
	}
	
	public static Object call(JoyCallback callback, String cacheName) {
		return call(callback, use(cacheName));
	}
	
	private static Object call(JoyCallback callback, RedisResource cache) {
		Jedis jedis = cache.getThreadJedis();
		boolean notThreadLocalJedis = (jedis == null);
		if (notThreadLocalJedis) {
			jedis = cache.jedisPool.getResource();
			cache.setThreadJedis(jedis);
		}
		try {
			return callback.run(cache);
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally {
			if (notThreadLocalJedis) {
				cache.removeThreadJedis();
				jedis.close();
			}
		}
	}
	*/
	
}




