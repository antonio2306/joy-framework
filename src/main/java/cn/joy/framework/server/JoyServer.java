package cn.joy.framework.server;

import java.util.Locale;
import java.util.Properties;

import cn.joy.framework.kits.StringKit;

public abstract class JoyServer {
	private Properties serverVariables = new Properties();
	
	public void init(Properties config){
		serverVariables = config;
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
	
	public String getLocalServerTag(){
		return this.getVariable("app_local_server_tag");
	}
	
	public String getCenterServerTag(){
		return this.getVariable("app_center_server_tag", "center");
	}
	
	public String getCenterServerUrl(){
		return this.getVariable("app_center_server_url");
	}
	
	public String getDefaultAppServerUrl(){
		return this.getVariable("app_default_app_server_url");
	}
	
	public String getDefaultAppFileServerUrl(){
		return this.getVariable("app_default_app_file_server_url");
	}
	
	public String getPlugins(){
		return this.getVariable("plugins", "");	//spring jfinal
	}
	
	public String getMVCPlugin(){
		return this.getVariable("plugin_mvc", "spring");	//jfinal
	}
	
	public String getTransactionPlugin(){
		return this.getVariable("plugin_transaction", "spring");	//jfinal
	}
	
	public String getRoutePlugin(){
		return this.getVariable("plugin_route", "spring");	
	}
	
	public String getLoginIdParam(){
		return this.getVariable("param_loginid", "loginId");
	}
	
	public String getCompanyCodeParam(){
		return this.getVariable("param_companycode", "companyCode");
	}
	
	public String getAccessTokenParam(){
		return this.getVariable("param_accesstoken", "accessToken");
	}
	
	public String getCharset(){
		return this.getVariable("app_charset", "UTF-8");
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
}
