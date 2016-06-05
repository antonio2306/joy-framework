package cn.joy.plugin.test.qiniu;

import org.testng.annotations.Test;

import cn.joy.framework.core.JoyMap;
import cn.joy.framework.test.TestExecutor;
import cn.joy.plugin.qiniu.Qiniu;
import cn.joy.plugin.qiniu.QiniuBucket;

@Test(groups="case.qiniu", dependsOnGroups="case.init")
public class TestUpload {
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executePluginGroup("case.qiniu");
	}
	
	@Test(enabled = false)
	public void testSimpleUpload(){
		Qiniu.use().upload("D:/test.jpg", QiniuBucket.TYPE_IMAGE, JoyMap.createStringObject().put("x:company", "12335").put("x:ext", ".jpg").map());
	}

}
