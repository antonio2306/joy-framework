package cn.joy.framework.plugin.spring;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

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
	public static String MVC_OPEN_REQUEST_URL = "openservice.do";
	public static String MVC_BUSINESS_REQUEST_URL = "businesservice.do";
	
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

	public String getOpenRequestPath(String action, String params, Map<String, String> datas){
		String url = getRequestPath(MVC_OPEN_REQUEST_URL, action, params, datas);
		return SpringResource.getSecurityManager().secureOpenRequestURL(url);
	}
	
	public String getBusinessRequestPath(String action, String params, Map<String, String> datas){
		return getRequestPath(MVC_BUSINESS_REQUEST_URL, action, params, datas);
	}

	public void start(){
		
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
				logger.debug("doTransaction, ruleResult="+ruleResult.toJSON());
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

	public String getServerURLByServerTag(String serverType, String serverTag) {
		return SpringResource.getRouteStore().getServerURLByServerTag(serverType, serverTag);
	}

	public void storeServerURL(String serverType, String serverTag, String serverURL) {
		SpringResource.getRouteStore().storeServerURL(serverType, serverTag, serverURL);
	}
}
