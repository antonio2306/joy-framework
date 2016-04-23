package cn.joy.framework.support;
/**
 * 路由表读写接口
 * @author liyy
 * @date 2014-07-06
 */
public interface RouteStore {
	public void initRoute();
	
	public String getServerURL(String routeKey);

	public void storeServerURL(String routeKey, String serverURL);
}
