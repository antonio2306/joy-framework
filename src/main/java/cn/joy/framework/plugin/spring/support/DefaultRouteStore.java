package cn.joy.framework.plugin.spring.support;

import org.apache.log4j.Logger;

public class DefaultRouteStore implements RouteStore{
	private Logger logger = Logger.getLogger(DefaultRouteStore.class);

	public String getServerURLByServerTag(String serverType, String serverTag) {
		logger.warn("Empty Impl...");
		return "";
	}

	public void storeServerURL(String serverType, String serverTag, String serverURL) {
		logger.warn("Empty Impl...");
	}

}
