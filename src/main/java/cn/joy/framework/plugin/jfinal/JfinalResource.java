package cn.joy.framework.plugin.jfinal;

import cn.joy.framework.plugin.spring.support.DefaultRouteStore;
import cn.joy.framework.plugin.spring.support.DefaultSecurityManager;
import cn.joy.framework.plugin.spring.support.RouteStore;
import cn.joy.framework.support.SecurityManager;
/**
 * JFinal资源注入
 * @author liyy
 * @date 2014-09-10
 */
public class JfinalResource {
	private static SecurityManager securityManager;
	private static RouteStore routeStore;
	public static String MVC_OPEN_REQUEST_URL = "open";
	public static String MVC_BUSINESS_REQUEST_URL = "business";
	
	public static SecurityManager getSecurityManager() {
		if(securityManager==null)
			securityManager = new DefaultSecurityManager();
		return securityManager;
	}

	public void setSecurityManager(SecurityManager securityManager) {
		JfinalResource.securityManager = securityManager;
	}

	public static RouteStore getRouteStore() {
		if(routeStore==null)
			routeStore = new DefaultRouteStore();
		return routeStore;
	}

	public void setRouteStore(RouteStore routeStore) {
		JfinalResource.routeStore = routeStore;
	}

}
