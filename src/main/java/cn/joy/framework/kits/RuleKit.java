package cn.joy.framework.kits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.exception.SubError;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.rule.RuleParam;
import cn.joy.framework.rule.RuleResult;

/**
 * 规则工具类
 * @author liyy
 * @date 2014-05-20
 */
public class RuleKit {
	private static Logger logger = Logger.getLogger(RuleKit.class);
	public final static String SIGNATURE_PARAM_NAME = "_sign";
	public final static String IGNORE_SIGNATURE_PARAM_NAME_PREFIX = "__";
	
	private static String getParam(HttpServletRequest request, String key){
		if(logger.isDebugEnabled())
			logger.debug("get request param, key="+key+", value="+request.getParameter(key));
		if("y".equals(request.getParameter("imr"))){
			String mergeKey = request.getParameter("mk");
			if(key.equals(mergeKey)){
				String value = StringKit.getString(request.getAttribute("MK_"+mergeKey));
				if(logger.isDebugEnabled())
					logger.debug("get param MK_"+mergeKey+", value="+value);
				if(StringKit.isEmpty(value))
					return request.getParameter(key);	//如果还没放MK_循环参数
				else
					return value;
			}
				
		}
		return request.getParameter(key);
	}
	
	public static String getStringParam(HttpServletRequest request, String key){
		return getStringParam(request, key, "");
	}
	
	public static String getStringParam(HttpServletRequest request, String key, String defaultValue){
		String value = getParam(request, key);
		if(value==null)
			return defaultValue;
		return value;
	}
	
	public static Long getLongParam(HttpServletRequest request, String key){
		return getLongParam(request, key, null);
	}
	
	public static Long getLongParam(HttpServletRequest request, String key, Long defaultValue){
		String value = getParam(request, key);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}
	
	public static Integer getIntParam(HttpServletRequest request, String key){
		return getIntParam(request, key, null);
	}
	
	public static Integer getIntParam(HttpServletRequest request, String key, Integer defaultValue){
		String value = getParam(request, key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}
	
	public static Boolean getBooleanParam(HttpServletRequest request, String key){
		return getBooleanParam(request, key, null);
	}
	
	public static Boolean getBooleanParam(HttpServletRequest request, String key, Boolean defaultValue){
		String value = getParam(request, key);
		try {
			return Boolean.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}
	
	public static String getStringAttribute(HttpServletRequest request, String key){
		return getStringAttribute(request, key, "");
	}
	
	public static String getStringAttribute(HttpServletRequest request, String key, String defaultValue){
		String value = StringKit.getString(request.getAttribute(key));
		if(StringKit.isEmpty(value))
			value = StringKit.getString(request.getSession().getAttribute(key));
		if(StringKit.isEmpty(value))
			return defaultValue;
		return value;
	}
	
	public static Long getLongAttribute(HttpServletRequest request, String key){
		return getLongAttribute(request, key, null);
	}
	
	public static Long getLongAttribute(HttpServletRequest request, String key, Long defaultValue){
		Long value = NumberKit.getLong(request.getAttribute(key));
		if(value==null)
			value = NumberKit.getLong(request.getSession().getAttribute(key));
		if(value==null)
			return defaultValue;
		return defaultValue;
	}
	
	public static String getStringParam(Map params, String key){
		return getStringParam(params, key, "");
	}
	
	public static String getStringParam(Map params, String key, String defaultValue){
		String value = StringKit.getString(params.get(key));
		if(value==null)
			return defaultValue;
		return value;
	}
	
	public static Long getLongParam(Map params, String key){
		return getLongParam(params, key, null);
	}
	
	public static Long getLongParam(Map params, String key, Long defaultValue){
		String value = StringKit.getString(params.get(key));
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}
	
	public static Boolean getBooleanParam(Map params, String key){
		return getBooleanParam(params, key, null);
	}
	
	public static Boolean getBooleanParam(Map params, String key, Boolean defaultValue){
		String value = StringKit.getString(params.get(key));
		try {
			return Boolean.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}
	
	public static String getSign(RuleParam rParam, String signKey){
		return getSign(rParam.getDatas(), signKey);
	}
	
	public static String getSign(Map<String, ?> params, String signKey){
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
	
	public static RuleResult checkSign(RuleParam rParam, String signKey){
		return checkSign(rParam.getDatas(), signKey);
	}
	
	public static RuleResult checkSign(Map<String, ?> params, String signKey){
		RuleResult result = RuleResult.create();
		
		Object sign = params.get(SIGNATURE_PARAM_NAME);
		if(StringKit.isEmpty(sign))
			return result.fail(SubError.createMain(SubErrorType.ISV_MISSING_PARAMETER, SIGNATURE_PARAM_NAME));
		if(!sign.equals(RuleKit.getSign(params, signKey)))
			return result.fail(SubError.createMain(SubErrorType.ISV_INVALID_PARAMETER, SIGNATURE_PARAM_NAME));
		return result.success();
	}
	
	public static void signResult(String signKey, RuleResult result){
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", StringKit.getString(result.isSuccess()));
		resultMap.put("content", StringKit.getString(result.getContent()));
		resultMap.put("msg", StringKit.getString(result.getMsg()));
		result.putExtraData(SIGNATURE_PARAM_NAME, getSign(resultMap, signKey));
	}
}
