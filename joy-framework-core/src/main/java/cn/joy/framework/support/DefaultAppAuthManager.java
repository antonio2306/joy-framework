package cn.joy.framework.support;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.exception.SubError;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.rule.RuleDispatcher;
import cn.joy.framework.rule.RuleResult;

public class DefaultAppAuthManager extends AppAuthManager{
	private Logger logger = Logger.getLogger(DefaultAppAuthManager.class);

	private static Map<String, String> appKeys = new HashMap<String, String>();

	@Override
	public RuleResult checkAPIRequest(HttpServletRequest request){
		String appKey = getAppKey(request);
		if(StringKit.isEmpty(appKey))
			return RuleResult.create().fail(SubError.createMain(SubErrorType.ISV_INVALID_PARAMETER, RuleDispatcher.APPID_PARAM_NAME));
		
		return RuleKit.checkSign(HttpKit.getParameterMap(request), appKey);
	}

	public String getAppKey(HttpServletRequest request){
		String appId = request.getParameter(RuleDispatcher.APPID_PARAM_NAME);
		String appKey = appKeys.get(appId);
		logger.debug("getAppKey, appId="+appId+", appKey="+appKey);
		if(appKey == null){
			appKey = this.getAppAuthInfoStore().getAppKey(appId);
			logger.debug("getAppKey, appId="+appId+", appKey="+appKey);
			if(StringKit.isEmpty(appKey)){
				logger.warn("App Key for " + appId + " not found");
				return "";
			} else
				appKeys.put(appId, appKey);
		}

		return appKey;
	}

	/*
	 * private String coverMap2String(Map<String, String> data){ if(data==null
	 * || data.size()==0) return "";
	 * 
	 * TreeMap<String, String> tree = new TreeMap<String, String>();
	 * for(Entry<String, String> entry:data.entrySet()){
	 * if("_sign".equals(entry.getKey().trim())){ continue; }
	 * tree.put(entry.getKey(), entry.getValue()); } if(tree.size()==0) return
	 * "";
	 * 
	 * StringBuilder sb = new StringBuilder(); for(Entry<String, String>
	 * entry:tree.entrySet()){
	 * sb.append(entry.getKey()).append("=").append(entry
	 * .getValue()).append("&"); }
	 * 
	 * return sb.substring(0, sb.length() - 1); }
	 */

	public static void main(String[] args){
		//http://xxxxxx/app/user/getUser?user=xx&code=1&state=0
		Map<String, Object> m = new HashMap();
		m.put("user", "xx");
		m.put("code", "1");
		m.put("state", "0");
		
		System.out.println(RuleKit.getSign(m, "abc"));
		System.out.println(cn.joy.framework.kits.EncryptKit.md5("code=1&state=0&user=xx&key=abc"));
	}
}
