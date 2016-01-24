package cn.joy.framework.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.StringKit;
/**
 * 服务器定义，加载服务器配置，支持一个中心服务器、多个应用服务器的部署结构
 * @author liyy
 * @date 2014-07-06
 */
public abstract class JoyServer {
	private Properties serverVariables = new Properties();
	
	public void init(Properties config){
		serverVariables = config;
		
		String envMode = this.getEnvMode();
		if(!"product".equals(envMode)){
			envMode = envMode+"_";
			for(String propName:serverVariables.stringPropertyNames()){
				if(propName.startsWith(envMode)){
					serverVariables.setProperty(propName.substring(envMode.length()), serverVariables.getProperty(propName));
				}
			}
		}
	}
	
	public void stop(){
		
	}
	
	public void setVariable(String key, String value){
		serverVariables.setProperty(key, value);
	}
	
	public String getVariable(String key){
		return getVariable(key, "");
	}
	
	public String getVariable(String key, String defaultValue){
		return StringKit.getString(serverVariables.getProperty(key), defaultValue);
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
	
	public String getAccessTokenParam(){
		return this.getVariable("param_accesstoken", "accessToken");
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
		return JoyManager.getSecurityManager().secureOpenRequestURL(null, url);
	}
	
	public String getBusinessRequestUrl(HttpServletRequest request, String serverURL, String params){
		String url = getRequestUrl(serverURL, getUrlBusiness(), params, null);
		return JoyManager.getSecurityManager().secureBusinessRequestURL(request, url);
	}
	
	public String getConfigRequestUrl(String serverURL, String params){
		String url = getRequestUrl(serverURL, getUrlConfig(), params, null);
		return JoyManager.getSecurityManager().secureOpenRequestURL(null, url);
	}
}
