package cn.joy.framework.plugin.spring.support;

public interface RouteStore {
	public String getServerURLByServerTag(String serverType, String serverTag);

	public void storeServerURL(String serverType, String serverTag, String serverURL);
}
