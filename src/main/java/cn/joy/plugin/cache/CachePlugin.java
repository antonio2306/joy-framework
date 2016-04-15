package cn.joy.plugin.cache;

import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.JoyPlugin;

public class CachePlugin extends JoyPlugin {

	@Override
	public void start() {
		String defaultProvider = getConfig().get("default_provider");
		if(StringKit.isNotEmpty(defaultProvider))
			Caches.DEFAULT_PROVIDER = defaultProvider;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
