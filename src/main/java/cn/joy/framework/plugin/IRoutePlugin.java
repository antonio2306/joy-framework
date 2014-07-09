package cn.joy.framework.plugin;

public interface IRoutePlugin extends IPlugin {
	public String getServerURLByServerTag(String serverTag);
	
	public void storeServerURL(String serverTag, String serverURL);
}
