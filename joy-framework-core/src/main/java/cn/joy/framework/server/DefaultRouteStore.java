package cn.joy.framework.server;

import java.util.List;
import java.util.Map;

import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;

public class DefaultRouteStore implements RouteStore{
	private static Log logger = LogKit.get();

	public String getServerURL(String routeKey) {
		logger.warn("Empty Impl...");
		return "";
	}

	public void storeServerURL(String routeKey, String serverURL) {
		logger.warn("Empty Impl...");
	}
	
	@Override
	public void loadLocalRoute(List routes) {
		logger.warn("Empty Impl...");
	}

	@Override
	public void loadCenterRoute(Map<String, Map> routeInfo) {
		logger.warn("Empty Impl...");
	}

	@Override
	public List listRoute() {
		logger.warn("Empty Impl...");
		return null;
	}

}
