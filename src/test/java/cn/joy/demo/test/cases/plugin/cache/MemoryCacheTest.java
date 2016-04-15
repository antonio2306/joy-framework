package cn.joy.demo.test.cases.plugin.cache;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import cn.joy.framework.core.JoyMap;
import cn.joy.framework.kits.NumberKit;
import cn.joy.framework.kits.Prop;
import cn.joy.framework.test.TestExecutor;
import cn.joy.plugin.cache.CacheProvider;
import cn.joy.plugin.cache.Caches;
import cn.joy.plugin.cache.provider.MemoryCache;

@Test(groups="case.cache", dependsOnGroups="case.init")
public class MemoryCacheTest {
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executeGroup("case.cache");
	}
	
	@Test(enabled = false)
	public void testMemoryCache(){
		CacheProvider cache = Caches.use("c1");
		
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
	}
	
	public void testMemoryCacheExpire(){
		CacheProvider cache = Caches.use("c2", new Prop(JoyMap.createStringObject().put("expire", 2)).getProperties());
		
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
