package cn.joy.framework.support;

public interface RouteStore {
	public String getServerURL(String serverTag);
	
	public void storeServerURL(String serverTag, String serverURL);
}
