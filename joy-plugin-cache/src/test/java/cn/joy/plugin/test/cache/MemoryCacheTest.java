package cn.joy.plugin.test.cache;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.provider.CacheProvider;
import cn.joy.framework.rule.RuleResult;
import cn.joy.framework.test.TestExecutor;

@Test(groups="case.cache", dependsOnGroups="case.init")
public class MemoryCacheTest {
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executePluginGroup("case.cache");
	}
	
	public void testCache(){
		CacheProvider<String, Object> cache = CacheProvider.use("c1");
		CacheProvider<String, User> cache2 = CacheProvider.use("c2");
		CacheProvider<String, String> cache3 = CacheProvider.use("c3");
		CacheProvider<String, Map> cache4 = CacheProvider.use("c4");
		
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
	
	public void testCacheExpire(){
		CacheProvider<String, Object> cache = CacheProvider.use("e1", null, 2, new JoyCallback() {
			@Override
			public RuleResult run(Object... params) throws Exception {
				System.out.println("expire key="+params[0]+", value="+params[1]);
				return null;
			}
		});
		
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
	
	public void testLoadingCache(){
		CacheProvider<String, Object> cache = CacheProvider.use("l1", new JoyCallback() {
			public Object run(Object... params) throws Exception {
				return "load "+params[0];
			};
		});
		
		cache.set("aa", 123);
		Assert.assertEquals(cache.get("aa"), 123);
		
		cache.del("aa");
		Assert.assertEquals(cache.get("aa"), "load aa");
		
		//map里的会触发load
		Assert.assertEquals(cache.hget("mm", "eee"), null);
		
		cache = CacheProvider.use("l2",
				new JoyCallback() {
					public Object run(Object... params) throws Exception {
						System.out.println("load...");
						return "load expire "+params[0];
					};
				}, 2);
		
		cache.set("aa", 234);
		Assert.assertEquals(cache.get("aa"), 234);
		
		try {
			Thread.sleep(2100);
		} catch (InterruptedException e) {
		}
		
		Assert.assertEquals(cache.get("aa"), "load expire aa");
		
	}
	
	@Test(enabled = false)
	public void testFrequence(){
		CacheProvider</* IP */String, Map<String, Integer>> cache = CacheProvider.use("smsIpInvoke", new JoyCallback() {
			@Override
			public Object run(Object... params) throws Exception {
				Map<String, Integer> countInfo = new HashMap<>();
				countInfo.put("c", 0);
				return countInfo;
			}
		}, 6);
		
		checkIpInvokeFrequency(cache, "192.168.1.100");
		checkIpInvokeFrequency(cache, "192.168.1.100");
		checkIpInvokeFrequency(cache, "192.168.1.100");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		checkIpInvokeFrequency(cache, "192.168.1.100");
		Assert.assertTrue(checkIpInvokeFrequency(cache, "192.168.1.100"));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		Assert.assertFalse(checkIpInvokeFrequency(cache, "192.168.1.100"));
		try {
			Thread.sleep(4100);
		} catch (InterruptedException e) {
		}
		Assert.assertTrue(checkIpInvokeFrequency(cache, "192.168.1.100"));
		Assert.assertTrue(checkIpInvokeFrequency(cache, "192.168.1.100"));
	}
	
	private boolean checkIpInvokeFrequency(CacheProvider<String, Map<String, Integer>> cache, String ip){
		Map<String, Integer> countInfo = cache.get(ip);
		System.out.println(countInfo);
		Integer count = countInfo.get("c");
		count++;
		if(count>5)
			return false;
		countInfo.put("c", count);
		return true;
	}
}
