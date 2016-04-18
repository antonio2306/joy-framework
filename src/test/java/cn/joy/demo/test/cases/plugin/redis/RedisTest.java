package cn.joy.demo.test.cases.plugin.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import cn.joy.framework.test.TestExecutor;
import cn.joy.plugin.redis.Cache;
import cn.joy.plugin.redis.Redis;

@Test(groups="case.redis", dependsOnGroups="case.init")
public class RedisTest {
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executeGroup("case.redis");
	}
	
	public void testDefaultRedis(){
		Cache cache = Redis.use();
		cache.openThreadJedis();
		//jedis关闭会还原
		cache.select(1);
		Assert.assertEquals(cache.getJedis().getDB().longValue(), 1);
		
		cache.set("abc", 123);
		Assert.assertEquals(cache.get("abc"), 123);
		
		//序列化后不能使用incr操作
		cache.setStr("num", "11");
		cache.incr("num");
		Assert.assertEquals(cache.getStr("num"), "12");
		
		cache.del("abc");
		Assert.assertEquals(cache.get("abc"), null);
		
		cache.mset("aa", 11, "bb", "22", "cc", "33");
		Assert.assertEquals(cache.get("bb"), "22");
		Assert.assertNotEquals(cache.get("bb"), 22);
		
		List list = cache.mget("aa", "bb", "cc");
		Assert.assertEquals(list.get(0), 11);
		
		list.add("44");
		cache.del("ee");
		cache.lpush("ee", list.toArray());
		
		Assert.assertTrue(cache.llen("ee")==4);
		Assert.assertEquals(cache.lindex("ee", 0), "44");
		
		Map map = new HashMap();
		map.put("m1", 111);
		map.put("m2", 222);
		map.put("m3", 333);
		map.put("m4", 444);
		
		cache.hmset("ff", map);
		Assert.assertEquals(cache.hget("ff", "m3"), 333);
		cache.hset("ff", "m5", 555);
		Assert.assertEquals(cache.hget("ff", "m5"), 555);
		cache.hdel("ff", "m2", "m3");
		Assert.assertNull(cache.hget("ff", "m2"));
		Assert.assertNull(cache.hget("ff", "m3"));
		
		//cache.hincrBy("ff", "m5", 1);
		//Assert.assertEquals(cache.hget("ff", "m5"), 556);
		//序列化的和不序列化的不能混用
		cache.hsetStr("gg", "m6", "99");
		cache.hincrByStr("gg", "m6", 1);
		Assert.assertEquals(cache.hgetStr("gg", "m6"), "100");
		//System.out.println(cache.hgetAllStr("ff").size());	//??? =2?
		//System.out.println(cache.hgetAll("ff").size());	//??? =2?
		
		Assert.assertEquals(cache.getJedis().getDB().longValue(), 1);
		cache.getJedis().flushDB();	//清空测试数据
		cache.release();
	}
}
