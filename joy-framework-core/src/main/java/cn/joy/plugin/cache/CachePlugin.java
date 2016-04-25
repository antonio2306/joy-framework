package cn.joy.plugin.cache;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.plugin.JoyPlugin;

@Plugin(key="cache")
public class CachePlugin extends JoyPlugin {

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
