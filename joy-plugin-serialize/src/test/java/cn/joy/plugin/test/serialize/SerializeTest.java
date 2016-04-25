package cn.joy.plugin.test.serialize;

import org.testng.Assert;
import org.testng.annotations.Test;

import cn.joy.framework.provider.SerializeProvider;
import cn.joy.framework.test.TestExecutor;

@Test(groups="case.serialize", dependsOnGroups="case.init")
public class SerializeTest {
	User user1;
	User user2;
	
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executePluginGroup("case.serialize");
	}
	
	public void initData(){
		user1 = new User().setName("张三").setAge(18).setGender("女");
		user2 = new User().setName("李四").setAge(20).setGender("男");
	}
	
	@Test(dependsOnMethods="initData")
	public void testDefaultSerialize(){
		SerializeProvider provider = SerializeProvider.use();
		
		byte[] user1Byte1 = provider.serialize(user1);
		Assert.assertEquals(provider.deserialize(user1Byte1, User.class).getName(), "张三");
		
		byte[] user2Byte1 = provider.serialize(user2);
		Assert.assertEquals(provider.deserialize(user2Byte1, User.class).getName(), "李四");
	}
	
	@Test(dependsOnMethods="initData")
	public void testProtostuffSerialize(){
		SerializeProvider provider = SerializeProvider.use("protostuff");
		
		byte[] user1Byte2 = provider.serialize(user1);
		Assert.assertEquals(provider.deserialize(user1Byte2, User.class).getName(), "张三");
		
		byte[] user2Byte2 = provider.serialize(user2);
		Assert.assertEquals(provider.deserialize(user2Byte2, User.class).getName(), "李四");
	}
}
