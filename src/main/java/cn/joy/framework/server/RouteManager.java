package cn.joy.framework.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.RuleException;
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
	private static Map<String, String> routes4Report = new HashMap<String, String>();

	public static List<String> getAllAppServerUrls(){
		List<String> urls = new ArrayList<String>();
		for(Entry<String, String> entry:routes.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			if(StringKit.isNotEmpty(value) && value.startsWith("http") && !urls.contains(value) &&!key.equals(CENTER_SERVER_TAG) 
					&& !key.equals("null") && !key.equals("undefined"))
				urls.add(value);
		}
		return urls;
	}

	public static String getServerTag(String companyCode) {
		if(companyCode==null || companyCode.equals("null"))
			return "";
		if(companyCode.indexOf(".")!=-1)	//仅为项目兼容
			return "";
		String tag = "";
		for (int i = 0; i < companyCode.length(); i++) {
			char c = companyCode.charAt(i);
			if (c >= 48 && c <= 57)
				break;
			tag += c;
		}
		return tag;
	}

	public static String getLocalServerTag() {
		return JoyManager.getServer().getLocalServerTag();
	}

	public static String getCenterServerURL() {
		String serverURL = routes.get(CENTER_SERVER_TAG);
		if (StringKit.isEmpty(serverURL)) {
			serverURL = JoyManager.getServer().getCenterServerUrl();
			routes.put(CENTER_SERVER_TAG, serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes: " + routes);
		return serverURL;
	}

	public static String getCenterFileServerURL() {
		String serverURL = routes4File.get(CENTER_SERVER_TAG);
		if (StringKit.isEmpty(serverURL)) {
			//serverURL = JoyManager.getServer().getCenterFileServerUrl();
			serverURL = JoyManager.getRoutePlugin().getServerURLByServerTag("file", CENTER_SERVER_TAG);
			if (StringKit.isEmpty(serverURL))
				throw new RuleException("Default Center File Server URL need init");
			routes4File.put(CENTER_SERVER_TAG, serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes4File: " + routes4File);
		return serverURL;
	}

	public static String getDefaultAppServerURL() {
		String serverURL = routes.get("app");
		if (StringKit.isEmpty(serverURL)) {
			if (JoyManager.getServer() instanceof CenterServer) {
				//serverURL = JoyManager.getServer().getDefaultAppServerUrl();
				serverURL = JoyManager.getRoutePlugin().getServerURLByServerTag("app", "app");
				if (StringKit.isEmpty(serverURL))
					throw new RuleException("Default App Server URL need init");
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
		if (StringKit.isEmpty(serverURL)) {
			if (JoyManager.getServer() instanceof CenterServer) {
				//serverURL = JoyManager.getServer().getDefaultAppFileServerUrl();
				serverURL = JoyManager.getRoutePlugin().getServerURLByServerTag("file", "app");
				if (StringKit.isEmpty(serverURL))
					throw new RuleException("Default App File Server URL need init");
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
	
	public static String getDefaultAppReportServerURL() {
		String serverURL = routes4Report.get("report");
		if (StringKit.isEmpty(serverURL)) {
			if (JoyManager.getServer() instanceof CenterServer) {
				serverURL = JoyManager.getRoutePlugin().getServerURLByServerTag("report", "app");
				if (StringKit.isEmpty(serverURL))
					throw new RuleException("Default App Report Server URL need init");
			} else {
				serverURL = HttpKit.get(getCenterServerURL()
						+ "/"
						+ JoyManager.getMVCPlugin().getOpenRequestPath(null, "getConfig", "&key=get_default_app_report_url",
								null));
			}
			routes4Report.put("app", serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes4Report: " + routes4Report);
		return serverURL;
	}

	public static String getServerURLByTag(String serverTag) {
		if (StringKit.isEmpty(serverTag)) // 空tag，则为默认应用服务器
			return getDefaultAppServerURL();
		
		if (CENTER_SERVER_TAG.equals(serverTag)) 
			return getCenterServerURL();

		String serverURL = routes.get(serverTag);
		if (StringKit.isEmpty(serverURL)) {
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
	
	public static String getReportServerURLByTag(String serverTag) {
		if (StringKit.isEmpty(serverTag)) // 空tag，则为默认应用文件服务器
			return getDefaultAppReportServerURL();
		
		String serverURL = routes4Report.get(serverTag);
		if (serverURL == null) {
			if (JoyManager.getServer() instanceof CenterServer) {
				serverURL = JoyManager.getRoutePlugin().getServerURLByServerTag("report", serverTag);
			} else {
				serverURL = HttpKit.get(getCenterServerURL()
						+ "/"
						+ JoyManager.getMVCPlugin().getOpenRequestPath(null, "getConfig",
								"&key=get_app_report_url&tag=" + serverTag, null));
				if(!serverURL.startsWith("http"))
					serverURL = "";
				else
					JoyManager.getRoutePlugin().storeServerURL("report", serverTag, serverURL);
			}
			routes4Report.put(serverTag, serverURL);
		}
		if (logger.isDebugEnabled())
			logger.debug("routes4Report: " + routes4Report);
		return serverURL;
	}

	public static String getServerURLByCompanyCode(String companyCode) {
		return getServerURLByTag(getServerTag(companyCode));
	}

	public static String getFileServerURLByCompanyCode(String companyCode) {
		return getFileServerURLByTag(getServerTag(companyCode));
	}
	
	public static String getReportServerURLByCompanyCode(String companyCode) {
		return getReportServerURLByTag(getServerTag(companyCode));
	}

	public static Map<String, String> getRoutes() {
		return routes;
	}

	public static Map<String, String> getRoutes4File() {
		return routes4File;
	}
	
	public static Map<String, String> getRoutes4Report() {
		return routes4Report;
	}
}
