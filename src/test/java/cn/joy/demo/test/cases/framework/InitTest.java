package cn.joy.demo.test.cases.framework;

import org.testng.annotations.Test;

import cn.joy.framework.core.JoyManager;

@Test(groups="case.init")
public class InitTest {

	public void joyStart() throws Exception{
		JoyManager.init();
	}
}
