package cn.joy.framework.plugin;

public interface IRoutePlugin extends IPlugin {
	public String getServerURLByServerTag(String serverType, String serverTag);
	
	public void storeServerURL(String serverType, String serverTag, String serverURL);
}
