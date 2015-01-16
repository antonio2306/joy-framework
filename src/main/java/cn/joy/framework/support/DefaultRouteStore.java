package cn.joy.framework.support;

import java.util.Map;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.server.RouteManager;

public class DefaultRouteStore implements RouteStore{
	private Logger logger = Logger.getLogger(DefaultRouteStore.class);

	public String getServerURL(String routeKey) {
		logger.warn("Empty Impl...");
		return "";
	}

	public void storeServerURL(String routeKey, String serverURL) {
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
						"&_t=route&_k=sync_route", null));
		if(logger.isDebugEnabled())
			logger.debug("routeInfo="+routeInfo);
		return JsonKit.json2Map(routeInfo);
	}

}
