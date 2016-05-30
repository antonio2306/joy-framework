package cn.joy.framework.server;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DefaultRouteStore implements RouteStore{
	private Logger logger = Logger.getLogger(DefaultRouteStore.class);

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
