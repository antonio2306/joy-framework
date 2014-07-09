package cn.joy.framework.server;

import java.util.HashMap;
import java.util.Map;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.StringKit;
/**
 * 网站服务器路由管理器
 * @author liyy
 * @date 2014-06-11
 */
public class RouteManager {
	public static final String CENTER_SERVER_TAG = JoyManager.getServer().getCenterServerTag();
	private static Map<String, String> routes = new HashMap<String, String>();
	
	public static String getServerTag(String qyescode){
		String tag = "";
		for(int i=0;i<qyescode.length();i++){
			char c = qyescode.charAt(i);
			if(c>=48&&c<=57)
				break;
			tag += c;
		}
		return tag;
	}
	
	public static String getServerTag(){
		return JoyManager.getServer().getLocalServerTag();
	}
	
	public static String getCenterServerURL(){
		String serverURL = routes.get(CENTER_SERVER_TAG);
		if(serverURL==null){
			serverURL = JoyManager.getServer().getCenterServerUrl();
			routes.put(CENTER_SERVER_TAG, serverURL);
		}
		return serverURL;
	}
	
	public static String getDefaultAppServerURL(){
		String serverURL = routes.get("app");
		if(serverURL==null){
			if(JoyManager.getServer() instanceof CenterServer){
				serverURL = JoyManager.getServer().getDefaultAppServerUrl();
			}else{
				serverURL = HttpKit.get(getCenterServerURL()+"/"+JoyManager.getMVCPlugin().getOpenRequestPath("getConfig", 
						"&key=get_default_app_url", null));
			}
			routes.put("brm", serverURL);
		}
		return serverURL;
	}
	
	public static String getServerURLByTag(String serverTag){
		if(StringKit.isEmpty(serverTag))	//空tag，则为默认应用服务器
			return getDefaultAppServerURL();
		
		String serverURL = routes.get(serverTag);
		if(serverURL==null){
			if(JoyManager.getServer() instanceof CenterServer){
				serverURL = JoyManager.getRoutePlugin().getServerURLByServerTag(serverTag);
			}else{
				serverURL = HttpKit.get(getCenterServerURL()+"/"+JoyManager.getMVCPlugin().getOpenRequestPath("getConfig", 
						"&key=get_app_url&tag="+serverTag, null));
				JoyManager.getRoutePlugin().storeServerURL(serverTag, serverURL);
			}
			routes.put(serverTag, serverURL);
		}
		return serverURL;
	}

	public static String getServerURLByQyescode(String qyescode){
		return getServerURLByTag(getServerTag(qyescode));
	}

}
