package cn.joy.framework.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;

/**
 * 网站服务器路由管理器
 * 
 * @author liyy
 * @date 2014-06-11
 */
public class RouteManager {
	private static Logger logger = Logger.getLogger(RouteManager.class);
	private static Map<String, String> routes = new HashMap<String, String>();
	private static Map<String, Properties> serverProps = new HashMap<String, Properties>();
	private static RouteStore routeStore = new DefaultRouteStore();
	private static boolean started;
	
	public static RouteStore getRouteStore() {
		//if(routeStore==null)
		//	routeStore = new DefaultRouteStore();
		return routeStore;
	}

	public static void setRouteStore(RouteStore routeStore) {
		RouteManager.routeStore = routeStore;
	}
	
	public static void start(){
		if(started)
			return;
		
		if(!loadFromLocalStore())
			loadFromRemoteStore();
		
		started = true;
	}
	
	public static boolean loadFromLocalStore(){
		List routes = routeStore.listRoute();
		if(routes==null || routes.isEmpty())
			return false;
		
		routeStore.loadLocalRoute(routes);
		return true;
	}
	
	public static boolean loadFromRemoteStore(){
		if(JoyManager.getServer().isCenterServer())
			return false;
			
		Map<String, Map> routeInfo = syncRouteInfo();
		if(routeInfo==null)
			return false;
		routeStore.loadCenterRoute(routeInfo);
		return true;
	}
	
	private static Map<String, Map> syncRouteInfo(){
		String serverURL = RouteManager.getCenterServerURL();
		if(StringKit.isNotEmpty(serverURL)){
			String routeInfo = HttpKit.get(JoyManager.getServer().getConfigRequestUrl(
					RouteManager.getCenterServerURL(), "_t=route&_k=sync_route&"+RuleKit.SERVER_KEY_PARAM_NAME+"="+RouteManager.getLocalRouteKey()));
					
			if(logger.isDebugEnabled())
				logger.debug("routeInfo="+routeInfo);
			return JsonKit.json2Map(routeInfo);
		}
		return null;
	}

	public static List<String> getAllServerUrls(String serverType){
		List<String> urls = new ArrayList<String>();
		for(Entry<String, String> entry:routes.entrySet()){
			String key = entry.getKey();
			if(!key.startsWith(serverType+":"))
				continue;
			String value = entry.getValue();
			if(StringKit.isNotEmpty(value) && value.startsWith("http") && !urls.contains(value) &&!key.equals(JoyManager.getServer().getCenterServerTag()) 
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
	
	public static String getLocalRouteKey() {
		return getRouteKey(JoyManager.getServer().getAppServerType(), JoyManager.getServer().getLocalServerTag());
	}
	
	public static String getRouteKey(String serverType, String serverTag){
		if (JoyManager.getServer().getCenterServerTag().equals(serverTag)){
			serverType = "center";
		}else{
			serverTag = StringKit.getString(serverTag, "app");
		}
		return serverType+":"+serverTag;
	}
	
	public static boolean isCenterRouteKey(String routeKey){
		return routeKey!=null && routeKey.startsWith("center:");
	}
	
	public static String getCenterRouteKey() {
		return getRouteKey("center", JoyManager.getServer().getCenterServerTag());
	}

	public static String getCenterServerURL() {
		return getCenterServerURLByKey(getRouteKey("center", JoyManager.getServer().getCenterServerTag()));
	}
	
	public static String getCenterServerURL(String serverType) {
		return getCenterServerURLByKey(getRouteKey(StringKit.getString(serverType, "center"), JoyManager.getServer().getCenterServerTag()));
	}

	public static String getCenterServerURLByKey(String routeKey) {
		if("false".equals(JoyManager.getServer().getVariable("app_center_server_enable")))
			return "";
		String serverURL = routes.get(routeKey);
		if (StringKit.isEmpty(serverURL)) {
			try{
				if(getRouteKey("center", JoyManager.getServer().getCenterServerTag()).equals(routeKey)){
					serverURL = JoyManager.getServer().getCenterServerUrl();
				}else{
					serverURL = routeStore.getServerURL(routeKey);
					if (StringKit.isEmpty(serverURL))
						serverURL = JoyManager.getServer().getCenterServerUrl();
				}
				if (StringKit.isEmpty(serverURL))
					throw new RuleException("Center Server URL for "+routeKey+" need init");
				routes.put(routeKey, serverURL);
			} catch(Exception e){
				logger.error("", e);
				return "";
			}
		}
		if (logger.isDebugEnabled())
			logger.debug("routes: " + routes);
		return serverURL;
	}
	
	public static String getAppServerURL() {
		return getAppServerURLByKey(getRouteKey("app", "app"));
	}
	
	public static String getAppServerURL(String serverType) {
		return getAppServerURLByKey(getRouteKey(StringKit.getString(serverType, "app"), "app"));
	}
	
	public static String getAppServerURL(String serverType, String serverTag) {
		return getAppServerURLByKey(getRouteKey(StringKit.getString(serverType, "app"), StringKit.getString(serverTag, "app")));
	}

	public static String getAppServerURLByKey(String routeKey) {
		String serverURL = routes.get(routeKey);
		if (StringKit.isEmpty(serverURL) && !JoyManager.getServer().isPrivateMode()) {
			if (JoyManager.getServer().isCenterServer()) {
				serverURL = routeStore.getServerURL(routeKey);
				if (StringKit.isEmpty(serverURL)){
					//throw new RuleException("App Server URL for "+routeKey+" need init");
					logger.warn("App Server URL for "+routeKey+" need init");
					return "";
				}
			} else {
				String centerURL = getCenterServerURL();
				if(StringKit.isNotEmpty(centerURL)){
					serverURL = HttpKit.get(JoyManager.getServer().getConfigRequestUrl(centerURL, "_t=route&_k="+routeKey+"&"+RuleKit.SERVER_KEY_PARAM_NAME+"="+RouteManager.getLocalRouteKey()));
					if(!serverURL.startsWith("http"))
						serverURL = "";
					else
						routeStore.storeServerURL(routeKey, serverURL);
				}
			}
			routes.put(routeKey, serverURL);
		}
		if (StringKit.isEmpty(serverURL) && logger.isDebugEnabled())
			logger.debug("routes: " + routes);
		return serverURL;
	}
	
	public static String getServerURLByTag(String serverTag) {
		if (JoyManager.getServer().getCenterServerTag().equals(serverTag)) 
			return getCenterServerURL();
		
		return getAppServerURL("app", serverTag);
	}
	
	public static String getServerURL(String serverType, String serverTag) {
		return getServerURLByKey(getRouteKey(serverType, serverTag));
	}
	
	public static String getServerURLByCompanyCode(String companyCode) {
		return getServerURLByTag(getServerTag(companyCode));
	}

	public static String getServerURLByCompanyCode(String serverType, String companyCode) {
		return getServerURL(serverType, getServerTag(companyCode));
	}
	
	public static String getServerURLByKey(String routeKey) {
		if (isCenterRouteKey(routeKey)) 
			return getCenterServerURLByKey(routeKey);

		return getAppServerURLByKey(routeKey);
	}

	public static Map<String, String> getRoutes() {
		return routes;
	}
	
	public static void setRoute(String routeKey, String serverURL){
		routes.put(routeKey, serverURL);
	}
	
	public static Map<String, Properties> getServerProps(){
		return serverProps;
	}
	
	public static String getServerProp(String routeKey, String propKey){
		Properties serverProp = serverProps.get(routeKey);
		if(serverProp!=null){
			return StringKit.getString(serverProp.get(propKey));
		}
		return "";
	}

	public static void setServerProp(String routeKey, String propKey, String propVal){
		Properties serverProp = serverProps.get(routeKey);
		if(serverProp==null){
			serverProp = new Properties();
			serverProps.put(routeKey, serverProp);
		}
		serverProp.put(propKey, StringKit.getString(propVal));
	}
}
