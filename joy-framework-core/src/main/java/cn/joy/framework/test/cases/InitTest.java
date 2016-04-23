package cn.joy.framework.test.cases;

import org.testng.annotations.Test;

import cn.joy.framework.core.JoyManager;

@Test(groups="case.init")
public class InitTest {

	public void joyStart() throws Exception{
		JoyManager.init();
	}
}
