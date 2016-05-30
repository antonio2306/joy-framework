package cn.joy.framework.server;

import java.util.List;
import java.util.Map;

/**
 * 路由表读写接口
 * @author liyy
 * @date 2014-07-06
 */
public interface RouteStore {
	public List listRoute();
	
	public void loadLocalRoute(List routes);
	
	public void loadCenterRoute(Map<String, Map> routeInfo);
	
	public String getServerURL(String routeKey);

	public void storeServerURL(String routeKey, String serverURL);
}
