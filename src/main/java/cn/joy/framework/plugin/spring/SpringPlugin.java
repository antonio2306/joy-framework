package cn.joy.framework.plugin.spring;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.RuleException;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.IMVCPlugin;
import cn.joy.framework.plugin.IRoutePlugin;
import cn.joy.framework.plugin.ITransactionPlugin;
import cn.joy.framework.plugin.spring.db.Db;
import cn.joy.framework.rule.RuleResult;
/**
 * Spring插件，提供MVC、事务、路由接口的实现
 * @author liyy
 * @date 2014-07-06
 */
public class SpringPlugin implements IMVCPlugin, ITransactionPlugin, IRoutePlugin{
	private static Logger logger = Logger.getLogger(SpringPlugin.class);
	
	private String getRequestPath(String baseURL, String action, String params, Map<String, String> datas){
		String url = baseURL+"?action="+action+StringKit.getString(params);
		if(datas!=null){
			try {
				for(Entry<String, String> entry:datas.entrySet()){
					url += "&"+entry.getKey()+"="+URLEncoder.encode(entry.getValue(), JoyManager.getServer().getCharset());
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}
		return url;
	}

	public String getOpenRequestPath(HttpServletRequest request, String action, String params, Map<String, String> datas){
		String url = getRequestPath(SpringResource.MVC_OPEN_REQUEST_URL, action, params, datas);
		return SpringResource.getSecurityManager().secureOpenRequestURL(request, url);
	}
	
	public String getBusinessRequestPath(HttpServletRequest request, String action, String params, Map<String, String> datas){
		String url = getRequestPath(SpringResource.MVC_BUSINESS_REQUEST_URL, action, params, datas);
		return SpringResource.getSecurityManager().secureBusinessRequestURL(request, url);
	}

	public void start(){
		SpringResource.getRouteStore().initRoute();
	}
	
	public void stop(){
		
	}

	public RuleResult doTransaction(JoyCallback callback) throws Exception{
		RuleResult ruleResult = null;
		boolean isNew = false;
		try {
			isNew = Db.beginTransaction();
			
			ruleResult = callback.run();
			if(logger.isDebugEnabled())
				logger.debug("doTransaction, result="+ruleResult.isSuccess());
			if(ruleResult.isSuccess()){
				if(isNew)
					Db.commitAndEndTransaction();
			}else
				throw new RuleException(ruleResult);
		} catch (Exception e) {
			if(e instanceof RuleException)
				logger.error("RuleException: "+e.getMessage());
			else
				logger.error("", e);
			if(isNew)
				Db.rollbackAndEndTransaction();
			throw e;
		} finally {
			if(isNew)
				Db.endTransaction();
		}
		return ruleResult;
	}

	public String getServerURL(String routeKey) {
		return SpringResource.getRouteStore().getServerURL(routeKey);
	}

	public void storeServerURL(String routeKey, String serverURL) {
		SpringResource.getRouteStore().storeServerURL(routeKey, serverURL);
	}
}
