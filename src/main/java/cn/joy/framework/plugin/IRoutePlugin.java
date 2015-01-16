package cn.joy.framework.plugin;
/**
 * 路由插件接口
 * @author liyy
 * @date 2014-07-06
 */
public interface IRoutePlugin extends IPlugin {
	public String getServerURL(String routeKey);
	
	public void storeServerURL(String routeKey, String serverURL);
}
