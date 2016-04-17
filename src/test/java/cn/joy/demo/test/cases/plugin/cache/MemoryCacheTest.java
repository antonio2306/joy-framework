package cn.joy.demo.test.cases.plugin.cache;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import cn.joy.demo.center.module.user.model.User;
import cn.joy.framework.core.JoyMap;
import cn.joy.framework.kits.Prop;
import cn.joy.framework.provider.CacheProvider;
import cn.joy.framework.test.TestExecutor;

@Test(groups="case.cache", dependsOnGroups="case.init")
public class MemoryCacheTest {
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executeGroup("case.cache");
	}
	
	public void testMemoryCache(){
		CacheProvider<String, Object> cache = CacheProvider.build("c1");
		CacheProvider<String, User> cache2 = CacheProvider.build("c2");
		CacheProvider<String, String> cache3 = CacheProvider.build("c3");
		CacheProvider<String, Map> cache4 = CacheProvider.build("c4");
		
		cache.set("aa", 123);
		Assert.assertEquals(cache.get("aa"), 123);
		
		User user = new User().setName("张三").setAge(18).setGender("女");
		cache2.set("zs", user);
		Assert.assertEquals(cache2.get("zs").getName(), "张三");
		
		cache.hset("mm", "abc", 111);
		cache3.hset("mm", "abc", 333);
		cache4.hset("mm", "abc", 444);
		Assert.assertEquals(cache.hget("mm", "abc"), 111);
		Assert.assertEquals(cache3.hget("mm", "abc"), 333);
		Assert.assertEquals(cache4.hget("mm", "abc"), 444);
		
		try {
			Thread.sleep(1200);
		} catch (InterruptedException e) {
		}
		
		Assert.assertEquals(cache.get("aa"), 123);
		Assert.assertEquals(cache2.get("zs").getName(), "张三");
		Assert.assertEquals(cache.hget("mm", "abc"), 111);
		Assert.assertEquals(cache3.hget("mm", "abc"), 333);
		Assert.assertEquals(cache4.hget("mm", "abc"), 444);
	}
	
	public void testMemoryCacheExpire(){
		CacheProvider<String, Object> cache = CacheProvider.build("e1", new Prop(JoyMap.createStringObject().put("expire", 2)).getProperties());
		
		cache.set("aa", 123);
		Assert.assertEquals(cache.get("aa"), 123);
		
		cache.hset("mm", "ddd", 222);
		cache.hset("mm", "abc", 111);
		Assert.assertEquals(cache.hget("mm", "abc"), 111);
		
		try {
			Thread.sleep(1200);
		} catch (InterruptedException e) {
		}
		
		Assert.assertEquals(cache.get("aa"), 123);
		Assert.assertEquals(cache.hget("mm", "abc"), 111);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		Assert.assertNull(cache.get("aa"));
		Assert.assertNull(cache.hget("mm", "abc"));
	}
}
