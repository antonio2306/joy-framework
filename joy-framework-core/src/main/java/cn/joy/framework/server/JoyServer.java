package cn.joy.framework.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.PropKit;
import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.kits.StringKit;
/**
 * 服务器定义，加载服务器配置，支持一个中心服务器、多个应用服务器的部署结构
 * @author liyy
 * @date 2014-07-06
 */
public class JoyServer {
	private static Log logger = LogKit.getLog(JoyServer.class);
	private static JoyServer server;
	private boolean started;
	private Prop serverProp;
	private String serverType;
	
	private JoyServer(){}
	
	public static JoyServer build(){
		if(server==null){
			server = new JoyServer();
			server.serverProp = PropKit.empty().set("app_local_server_type", "none");
		}
		return server;
	}
	
	public void start(){
		if(started)
			return;
		
		if(!loadConfig()){
			//throw new RuntimeException("No JOY config file exists!");
			//没有配置文件则不启用JOY框架
			logger.warn("No JOY config file exists!");
			return;
		}
		
		logger.info(serverType+" server start, config="+serverProp.getProperties());
		started = true;
	}
	
	public void stop(){
		
	}
	
	public boolean loadConfig(){
		if(started){
			try {
				String propFile = isCenterServer()?"joy-center.cnf":"joy-app.cnf";
				PropKit.useless(propFile);
				serverProp.setAll(PropKit.use(propFile));
			} catch (Exception e) {
				return false;
			}
		}else{
			try {
				serverProp = PropKit.use("joy-center.cnf");
				serverType = "center";
			} catch (Exception e) {
				try {
					serverProp = PropKit.use("joy-app.cnf");
					serverType = "app";
				} catch (Exception e1) {
				}
			}
			
			if(serverProp==null)
				return false;
		}
		
		String envMode = getEnvMode();
		if(!"product".equals(envMode)){
			envMode = envMode+"_";
			for(String propName:serverProp.keys()){
				if(propName.startsWith(envMode)){
					serverProp.set(propName.substring(envMode.length()), serverProp.get(propName));
				}
			}
		}
		return true;
	}
	
	public Prop getConfig(){
		return serverProp;
	}
	
	public boolean isCenterServer(){
		return "center".equals(serverType);
	}
	
	public boolean isAppServer(){
		return !"center".equals(serverType);
	}
	
	public void setVariable(String key, String value){
		serverProp.set(key, value);
	}
	
	public String getVariable(String key){
		return getVariable(key, "");
	}
	
	public String getVariable(String key, String defaultValue){
		return serverProp.get(key, defaultValue);
	}
	
	public String getBasePackage(){
		return this.getVariable("app_base_package");
	}
	
	public String getModulePackage(){
		return this.getVariable("app_module_package");
	}
	
	public String getRuleURIPattern(){
		return this.getVariable("app_rule_uri_pattern", getModulePackage()+".%1$s.rule.%2$sRule");
	}
	
	public String getEventPackagePattern(){
		return this.getVariable("app_event_package_pattern", getModulePackage()+".%1$s.event");
	}
	
	public String getAppServerType(){
		return this.getVariable("app_local_server_type", "app");
	}
	
	public String getLocalServerTag(){
		return this.getVariable("app_local_server_tag");
	}
	
	public String getCenterServerTag(){
		return this.getVariable("app_center_server_tag", "center");
	}
	
	public String getCenterServerUrl(){
		return this.getVariable("app_center_server_url");
	}
	
	public String getPlugins(){
		return this.getVariable("plugins", "");	//spring jfinal
	}
	
	public String getTransactionPlugin(){
		return this.getVariable("plugin_transaction", "spring");	//jfinal
	}
	
	public String getLoginIdParam(){
		return this.getVariable("param_loginid", "loginId");
	}
	
	public String getCompanyCodeParam(){
		return this.getVariable("param_companycode", "companyCode");
	}
	
	public String getSceneKeyParam(){
		return this.getVariable("param_scenekey", "sceneKey");
	}
	
	public String getSessionSceneKeyParam(){
		return this.getVariable("param_scenekeyinsession", "sceneKeyInSession");
	}
	
	public String getAppIdParam(){
		return this.getVariable("param_appid", "appId");
	}
	
	public String getAccessTokenParam(){
		return this.getVariable("param_accesstoken", "accessToken");
	}
	
	public String getSourceIPParam(){
		return this.getVariable("param_sourceip", "sourceIP");
	}
	
	public String getCharset(){
		return this.getVariable("app_charset", "UTF-8");
	}
	
	public String getEnvMode(){
		return this.getVariable("env_mode", "product");
	}
	
	public Locale getLocale(){
		String locale = this.getVariable("app_locale", "cn");
		if(locale.equals("cn"))
			return Locale.SIMPLIFIED_CHINESE;
		else if(locale.equals("en"))
			return Locale.US;
		return Locale.getDefault();
	}

	public String getDefaultModule() {
		return this.getVariable("app_default_module");
	}
	
	public String getDefaultSceneKey() {
		return this.getVariable("app_default_scene_key");
	}
	
	public String getDebugLogDir() {
		return this.getVariable("debug_log_dir");
	}
	
	public String getApiLogDir() {
		return this.getVariable("api_log_dir");
	}
	
	private String getRequestUrl(String serverURL, String serviceURL, String params, Map<String, String> datas){
		String url = serverURL+"/"+serviceURL+"?"+StringKit.getString(params);
		if(datas!=null){
			try {
				for(Entry<String, String> entry:datas.entrySet()){
					url += "&"+entry.getKey()+"="+URLEncoder.encode(entry.getValue(), getCharset());
				}
			} catch (UnsupportedEncodingException e) {
			}
		}
		return url;
	}
	
	public String getUrlOpen(){
		return getVariable("url_open", "rs/o");
	}
	
	public String getUrlAPI(){
		return getVariable("url_api", "rs/a");
	}
	
	public String getUrlBusiness(){
		return getVariable("url_business", "rs/b");
	}
	
	public String getUrlConfig(){
		return getVariable("url_config", "rs/c");
	}
	
	public String getUrlDownload(){
		return getVariable("url_download", "rs/d");
	}
	
	public String getUrlWebProxy(){
		return getVariable("url_webproxy", "rs/wp");
	}
	
	public boolean isPrivateMode(){
		return "private".equals(getVariable("deploy_mode"));
	}
	
	public String getOpenRequestUrl(String serverURL, String params){
		String url = getRequestUrl(serverURL, getUrlOpen(), params, null);
		return url;//JoyManager.getSecurityManager().secureOpenRequestURL(null, url);
	}
	
	public String getBusinessRequestUrl(HttpServletRequest request, String serverURL, String params){
		String url = getRequestUrl(serverURL, getUrlBusiness(), params, null);
		return JoyManager.getSecurityManager().secureBusinessRequestURL(request, url);
	}
	
	public String getConfigRequestUrl(String serverURL, String params){
		String url = getRequestUrl(serverURL, getUrlConfig(), params, null);
		return url;//JoyManager.getSecurityManager().secureOpenRequestURL(null, url);
	}
}
