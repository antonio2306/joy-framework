package cn.joy.framework.plugin.jfinal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import cn.joy.framework.rule.RuleResult;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

public class JfinalPlugin implements IMVCPlugin, ITransactionPlugin, IRoutePlugin{
	private static Logger logger = Logger.getLogger(JfinalPlugin.class);
	
	public void start() {
		JfinalResource.getRouteStore().initRoute();
	}

	public void stop() {
	}

	public String getServerURLByServerTag(String serverType, String serverTag) {
		return JfinalResource.getRouteStore().getServerURLByServerTag(serverType, serverTag);
	}

	public void storeServerURL(String serverType, String serverTag, String serverURL) {
		JfinalResource.getRouteStore().storeServerURL(serverType, serverTag, serverURL);
	}
	
	public RuleResult doTransaction(final JoyCallback callback) throws Exception {
		RuleResult ruleResult = null;
		final List<RuleResult> resultWrap = new ArrayList<RuleResult>();
		try {
			Db.tx(new IAtom() {
				public boolean run() throws SQLException {
					try {
						RuleResult txResult = callback.run();
						if(txResult.isSuccess())
							resultWrap.add(txResult);
						else
							return false;
					} catch (Exception e) {
						logger.error("", e);
						return false;
					}
					return true;
				}
			});
			
			ruleResult = resultWrap.get(0);
			if(logger.isDebugEnabled())
				logger.debug("doTransaction, result="+ruleResult.isSuccess());
			if(ruleResult.isSuccess()){
			}else
				throw new RuleException(ruleResult);
		} catch (Exception e) {
			if(e instanceof RuleException)
				logger.error("RuleException: "+e.getMessage());
			else
				logger.error("", e);
			throw e;
		} finally {
		}
		return ruleResult;
	}
	
	private String getRequestPath(String baseURL, String action, String params, Map<String, String> datas){
		String url = baseURL+"/"+StringKit.getString(action, "index")+"?"+StringKit.getString(params);
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

	public String getOpenRequestPath(HttpServletRequest request, String action, String params, Map<String, String> datas) {
		String url = getRequestPath(JfinalResource.MVC_OPEN_REQUEST_URL, action, params, datas);
		return JfinalResource.getSecurityManager().secureOpenRequestURL(request, url);
	}

	public String getBusinessRequestPath(HttpServletRequest request, String action, String params,
			Map<String, String> datas) {
		String url = getRequestPath(JfinalResource.MVC_BUSINESS_REQUEST_URL, action, params, datas);
		return JfinalResource.getSecurityManager().secureBusinessRequestURL(request, url);
	}


}
