package cn.joy.framework.plugin.spring.support;

public interface RouteStore {
	public String getServerURLByServerTag(String serverTag);

	public void storeServerURL(String serverTag, String serverURL);
}
