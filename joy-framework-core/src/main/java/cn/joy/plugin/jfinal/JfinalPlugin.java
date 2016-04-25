package cn.joy.plugin.jfinal;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.plugin.JoyPlugin;

@Plugin(key="jfinal")
public class JfinalPlugin  extends JoyPlugin{
	
	public boolean start() {
		return true;
	}

	public void stop() {
	}

}
