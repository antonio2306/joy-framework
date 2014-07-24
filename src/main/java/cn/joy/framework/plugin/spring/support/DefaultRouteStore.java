package cn.joy.framework.plugin.spring.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.server.RouteManager;

public class DefaultRouteStore implements RouteStore{
	private Logger logger = Logger.getLogger(DefaultRouteStore.class);
	protected Map<String, String> routes = new HashMap<String, String>();

	public String getServerURLByServerTag(String serverType, String serverTag) {
		logger.warn("Empty Impl...");
		return "";
	}

	public void storeServerURL(String serverType, String serverTag, String serverURL) {
		logger.warn("Empty Impl...");
	}

	public void initRoute() {
		//同步路由数据，并加载到缓存
		logger.warn("Empty Impl...");
	}
	
	protected Map<String, Map<String, String>> syncRouteInfo(){
		String routeInfo = HttpKit.get(RouteManager.getCenterServerURL()
				+ "/"
				+ JoyManager.getMVCPlugin().getOpenRequestPath(null, "getConfig",
						"&key=sync_route", null));
		return JsonKit.json2Map(routeInfo);
	}

}
