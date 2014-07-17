package cn.joy.framework.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.StringKit;

/**
 * 网站服务器路由管理器
 * 
 * @author liyy
 * @date 2014-06-11
 */
public class RouteManager {
	private static Logger logger = Logger.getLogger(RouteManager.class);
	public static final String CENTER_SERVER_TAG = JoyManager.getServer().getCenterServerTag();
	private static Map<String, String> routes = new HashMap<String, String>();
	private static Map<String, String> routes4File = new HashMap<String, String>();

	public static List<String> getAllAppServerUrls(){
		List<String> urls = new ArrayList<String>();
		for(Entry<String, String> entry:routes.entrySet()){
			if(!entry.getKey().equals(CENTER_SERVER_TAG))
				urls.add(entry.getValue());
		}
		return urls;
	}

	public static String getServerTag(String qyescode) {
		String tag = "";
		for (int i = 0; i < qyescode.length(); i++) {
			char c = qyescode.charAt(i);
			if (c >= 48 && c <= 57)
				break;
			tag += c;
		}
		return tag;
	}

	public static String getServerTag() {
		return JoyManager.getServer().getLocalServerTag();
	}

	public static String getCenterServerURL() {
		String serverURL = routes.get(CENTER_SERVER_TAG);
		if (serverURL == null) {
			serverURL = JoyManager.getServer().getCenterServerUrl();
			routes.put(CENTER_SERVER_TAG, serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes: " + routes);
		return serverURL;
	}

	public static String getCenterFileServerURL() {
		String serverURL = routes4File.get(CENTER_SERVER_TAG);
		if (serverURL == null) {
			serverURL = JoyManager.getServer().getCenterFileServerUrl();
			routes4File.put(CENTER_SERVER_TAG, serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes4File: " + routes4File);
		return serverURL;
	}

	public static String getDefaultAppServerURL() {
		String serverURL = routes.get("app");
		if (serverURL == null) {
			if (JoyManager.getServer() instanceof CenterServer) {
				serverURL = JoyManager.getServer().getDefaultAppServerUrl();
			} else {
				serverURL = HttpKit.get(getCenterServerURL() + "/"
						+ JoyManager.getMVCPlugin().getOpenRequestPath(null, "getConfig", "&key=get_default_app_url", null));
			}
			routes.put("app", serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes: " + routes);
		return serverURL;
	}

	public static String getDefaultAppFileServerURL() {
		String serverURL = routes4File.get("app");
		if (serverURL == null) {
			if (JoyManager.getServer() instanceof CenterServer) {
				serverURL = JoyManager.getServer().getDefaultAppFileServerUrl();
			} else {
				serverURL = HttpKit.get(getCenterServerURL()
						+ "/"
						+ JoyManager.getMVCPlugin().getOpenRequestPath(null, "getConfig", "&key=get_default_app_file_url",
								null));
			}
			routes4File.put("app", serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes4File: " + routes4File);
		return serverURL;
	}

	public static String getServerURLByTag(String serverTag) {
		if (StringKit.isEmpty(serverTag)) // 空tag，则为默认应用服务器
			return getDefaultAppServerURL();
		
		if (CENTER_SERVER_TAG.equals(serverTag)) 
			return getCenterServerURL();

		String serverURL = routes.get(serverTag);
		if (serverURL == null) {
			if (JoyManager.getServer() instanceof CenterServer) {
				serverURL = JoyManager.getRoutePlugin().getServerURLByServerTag("app", serverTag);
			} else {
				serverURL = HttpKit.get(getCenterServerURL()
						+ "/"
						+ JoyManager.getMVCPlugin().getOpenRequestPath(null, "getConfig",
								"&key=get_app_url&tag=" + serverTag, null));
				if(!serverURL.startsWith("http"))
					serverURL = "";
				else
					JoyManager.getRoutePlugin().storeServerURL("app", serverTag, serverURL);
			}
			routes.put(serverTag, serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes: " + routes);
		return serverURL;
	}

	public static String getFileServerURLByTag(String serverTag) {
		if (StringKit.isEmpty(serverTag)) // 空tag，则为默认应用文件服务器
			return getDefaultAppFileServerURL();
		
		if (CENTER_SERVER_TAG.equals(serverTag)) 
			return getCenterFileServerURL();

		String serverURL = routes4File.get(serverTag);
		if (serverURL == null) {
			if (JoyManager.getServer() instanceof CenterServer) {
				serverURL = JoyManager.getRoutePlugin().getServerURLByServerTag("file", serverTag);
			} else {
				serverURL = HttpKit.get(getCenterServerURL()
						+ "/"
						+ JoyManager.getMVCPlugin().getOpenRequestPath(null, "getConfig",
								"&key=get_app_file_url&tag=" + serverTag, null));
				if(!serverURL.startsWith("http"))
					serverURL = "";
				else
					JoyManager.getRoutePlugin().storeServerURL("file", serverTag, serverURL);
			}
			routes4File.put(serverTag, serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes4File: " + routes4File);
		return serverURL;
	}

	public static String getServerURLByQyescode(String qyescode) {
		return getServerURLByTag(getServerTag(qyescode));
	}

	public static String getFileServerURLByQyescode(String qyescode) {
		return getFileServerURLByTag(getServerTag(qyescode));
	}

	public static Map<String, String> getRoutes() {
		return routes;
	}

	public static Map<String, String> getRoutes4File() {
		return routes4File;
	}
}
