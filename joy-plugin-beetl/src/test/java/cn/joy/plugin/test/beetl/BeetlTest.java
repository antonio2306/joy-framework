package cn.joy.plugin.test.beetl;

import java.io.File;

import org.testng.annotations.Test;

import cn.joy.framework.core.JoyMap;
import cn.joy.framework.test.TestExecutor;
import cn.joy.plugin.beetl.Beetl;

@Test(groups="case.beetl", dependsOnGroups="case.init")
public class BeetlTest {
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executePluginGroup("case.beetl");
	}
	
	@Test
	public void testFileTpl(){
		Beetl.use("test").merge("/"+BeetlTest.class.getPackage().getName().replace(".", "/")+"/test.tpl", JoyMap.createStringObject().put("name", "ray").map(), 
				new File("D:/test.txt"));
		System.out.println(112);
	}
}
