package cn.joy.framework.plugin.spring.support;
/**
 * 路由表读写接口
 * @author liyy
 * @date 2014-07-06
 */
public interface RouteStore {
	public String getServerURLByServerTag(String serverType, String serverTag);

	public void storeServerURL(String serverType, String serverTag, String serverURL);
}
