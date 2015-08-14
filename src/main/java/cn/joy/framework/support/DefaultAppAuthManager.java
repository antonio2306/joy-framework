package cn.joy.framework.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.exception.RuleException;
import cn.joy.framework.exception.SubError;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.kits.EncryptKit;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.rule.RuleResult;

public class DefaultAppAuthManager extends AppAuthManager{
	private Logger logger = Logger.getLogger(DefaultAppAuthManager.class);
	private final static String APPID_PARAM_NAME = "_appId";
	private final static String SIGNATURE_PARAM_NAME = "_sign";
	private final static String IGNORE_SIGNATURE_PARAM_NAME_PREFIX = "__";

	private static Map<String, String> appKeys = new HashMap<String, String>();

	@Override
	public RuleResult checkAPIRequest(HttpServletRequest request){
		RuleResult result = RuleResult.create();
		
		String appId = request.getParameter(APPID_PARAM_NAME);
		if(StringKit.isEmpty(appId))
			return result.fail(SubError.createMain(SubErrorType.ISV_MISSING_PARAMETER, APPID_PARAM_NAME));
		
		String appKey = getAppKey(request);
		if(StringKit.isEmpty(appKey))
			return result.fail(SubError.createMain(SubErrorType.ISV_INVALID_PARAMETER, APPID_PARAM_NAME));
		
		Map<String, String> params = HttpKit.getParameterMap(request);
		String sign = params.get(SIGNATURE_PARAM_NAME);
		if(StringKit.isEmpty(sign))
			return result.fail(SubError.createMain(SubErrorType.ISV_MISSING_PARAMETER, SIGNATURE_PARAM_NAME));
		if(!sign.equals(getSign(params, appKey)))
			return result.fail(SubError.createMain(SubErrorType.ISV_INVALID_PARAMETER, SIGNATURE_PARAM_NAME));
		return result.success();
	}

	@Override
	public void signAPIResult(HttpServletRequest request, RuleResult result){
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", StringKit.getString(result.isSuccess()));
		resultMap.put("content", StringKit.getString(result.getContent()));
		resultMap.put("msg", StringKit.getString(result.getMsg()));
		result.putExtraData(SIGNATURE_PARAM_NAME, getSign(resultMap, getAppKey(request)));
	}

	private String getAppKey(HttpServletRequest request){
		String appId = request.getParameter(APPID_PARAM_NAME);
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

	private String getSign(Map<String, ?> params, String signKey){
		logger.debug("params="+params+", signKey="+signKey);
		if(StringKit.isEmpty(signKey))
			return "";
		List<String> keyList = new ArrayList<String>(params.keySet());
		Collections.sort(keyList);

		StringBuilder str = new StringBuilder();
		for(String key : keyList){
			key = key.trim();
			if(key.equals(SIGNATURE_PARAM_NAME) || key.startsWith(IGNORE_SIGNATURE_PARAM_NAME_PREFIX))
				continue;
			Object value = params.get(key);
			if(StringKit.isNotEmpty(value))
				str.append(key).append("=").append(value.toString().trim()).append("&");
		}
		if(str.length() > 0)
			str.deleteCharAt(str.length() - 1);
		str.append("&key=").append(signKey);
		String sign = EncryptKit.md5(str.toString()).toLowerCase();
		logger.debug("sign="+sign);
		return sign;
	}
	
}
