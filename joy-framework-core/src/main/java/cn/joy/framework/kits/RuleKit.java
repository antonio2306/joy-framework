package cn.joy.framework.kits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.exception.SubError;
import cn.joy.framework.exception.SubErrorType;
import cn.joy.framework.rule.RuleContext;
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
	public final static String SERVER_KEY_PARAM_NAME = "_serverKey";
	public final static String SERVER_PROXY_PARAM_NAME = "_serverProxy";
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
	
	private static String[] getSplitArrayParam(HttpServletRequest request, String key){
		return getSplitArrayParam(request, key, ",");
	}
	
	private static String[] getSplitArrayParam(HttpServletRequest request, String key, String separator){
		String value = request.getParameter(key);
		if(value==null)
			return null;
		return value.split(separator);
	}
	
	/**
	 * 根据key获取request参数的字符串值
	 * 
	 * @param request
	 * @param key 参数名
	 * @return 如果是null，返回空字符串
	 */
	public static String getStringParam(HttpServletRequest request, String key){
		return getStringParam(request, key, "");
	}
	
	/**
	 * 根据key获取request参数的字符串数组值
	 * 
	 * @param request
	 * @param key 参数名
	 * @return 根据key获取到value后，按逗号分隔成数组；如果是null，返回空字符串数组
	 */
	public static String[] getStringArrayParam(HttpServletRequest request, String key){
		return getSplitArrayParam(request, key);
	}
	
	/**
	 * 根据key获取request参数的字符串数组值
	 * 
	 * @param request
	 * @param key 参数名
	 * @param separator 指定分隔符
	 * @return 根据key获取到value后，按指定分隔符分隔成数组；如果是null，返回空字符串数组
	 */
	public static String[] getStringArrayParam(HttpServletRequest request, String key, String separator){
		return getSplitArrayParam(request, key, separator);
	}
	
	/**
	 * 根据key获取request参数的字符串值
	 * 
	 * @param request
	 * @param key 参数名
	 * @param defaultValue 默认值
	 * @return 如果是null，返回默认值
	 */
	public static String getStringParam(HttpServletRequest request, String key, String defaultValue){
		String value = getParam(request, key);
		if(value==null)
			return defaultValue;
		return value;
	}
	
	/**
	 * 根据key获取request参数的长整型值
	 * 
	 * @param request
	 * @param key 参数名
	 * @return 
	 */
	public static Long getLongParam(HttpServletRequest request, String key){
		return getLongParam(request, key, null);
	}
	
	/**
	 * 根据key获取request参数的长整型值
	 * 
	 * @param request
	 * @param key 参数名
	 * @param defaultValue 默认值
	 * @return 如果是null，返回默认值
	 */
	public static Long getLongParam(HttpServletRequest request, String key, Long defaultValue){
		String value = getParam(request, key);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}
	
	/**
	 * 根据key获取request参数的整型值
	 * 
	 * @param request
	 * @param key 参数名
	 * @return 
	 */
	public static Integer getIntParam(HttpServletRequest request, String key){
		return getIntParam(request, key, null);
	}
	
	/**
	 * 根据key获取request参数的整型值
	 * 
	 * @param request
	 * @param key 参数名
	 * @param defaultValue 默认值
	 * @return 如果是null，返回默认值
	 */
	public static Integer getIntParam(HttpServletRequest request, String key, Integer defaultValue){
		String value = getParam(request, key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}
	
	/**
	 * 根据key获取request参数的布尔值
	 * 
	 * @param request
	 * @param key 参数名
	 * @return 
	 */
	public static Boolean getBooleanParam(HttpServletRequest request, String key){
		return getBooleanParam(request, key, null);
	}
	
	/**
	 * 根据key获取request参数的布尔值
	 * 
	 * @param request
	 * @param key 参数名
	 * @param defaultValue 默认值
	 * @return 如果是null，返回默认值
	 */
	public static Boolean getBooleanParam(HttpServletRequest request, String key, Boolean defaultValue){
		String value = getParam(request, key);
		try {
			return Boolean.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return defaultValue;
	}
	
	/**
	 * 根据key获取request属性的字符串值
	 * 
	 * @param request
	 * @param key 参数名
	 * @return 如果是null，返回空字符串
	 */
	public static String getStringAttribute(HttpServletRequest request, String key){
		return getStringAttribute(request, key, "");
	}
	
	/**
	 * 根据key获取request属性的字符串值
	 * 
	 * @param request
	 * @param key 参数名
	 * @param defaultValue 默认值
	 * @return 如果是null，返回默认值
	 */
	public static String getStringAttribute(HttpServletRequest request, String key, String defaultValue){
		String value = StringKit.getString(request.getAttribute(key));
		if(StringKit.isEmpty(value))
			value = StringKit.getString(request.getSession().getAttribute(key));
		if(StringKit.isEmpty(value))
			return defaultValue;
		return value;
	}
	
	/**
	 * 根据key获取request属性的长整型值
	 * 
	 * @param request
	 * @param key 参数名
	 * @return 
	 */
	public static Long getLongAttribute(HttpServletRequest request, String key){
		return getLongAttribute(request, key, null);
	}
	
	/**
	 * 根据key获取request属性的长整型值
	 * 
	 * @param request
	 * @param key 参数名
	 * @param defaultValue 默认值
	 * @return 如果是null，返回默认值
	 */
	public static Long getLongAttribute(HttpServletRequest request, String key, Long defaultValue){
		Long value = TypeKit.toLong(request.getAttribute(key));
		if(value==null)
			value = TypeKit.toLong(request.getSession().getAttribute(key));
		if(value==null)
			return defaultValue;
		return defaultValue;
	}
	
	/**
	 * 计算签名
	 * 
	 * @param rParam 参与计算的参数
	 * @param signKey 用于计算签名的密钥
	 * @return
	 */
	public static String getSign(RuleParam rParam, String signKey){
		return getSign(rParam.getDatas(), signKey);
	}
	
	/**
	 * 计算签名
	 * 
	 * 计算方法：排除掉_sign和以__开头的参数名，排除掉值为空和值不是java原生类型的，剩余参数按参数名的字母序升序排列后，通过=和&拼接，
	 * 			再加上&key=密钥后计算md5并转为小写
	 * 
	 * @param params 参与计算的参数
	 * @param signKey 用于计算签名的密钥
	 * @return
	 */
	public static String getSign(Map<String, ?> params, String signKey){
		if(logger.isDebugEnabled())
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
			if(StringKit.isNotEmpty(value) && TypeKit.isJavaType(value.getClass()))
				str.append(key).append("=").append(value.toString().trim()).append("&");
		}
		if(str.length() > 0)
			str.deleteCharAt(str.length() - 1);
		str.append("&key=").append(signKey);
		String sign = EncryptKit.md5(str.toString()).toLowerCase();
		if(logger.isDebugEnabled())
			logger.debug("get sign="+sign);
		return sign;
	}
	
	/**
	 * 验证签名
	 * 
	 * @param rParam 参与验证的参数
	 * @param signKey 用于验证签名的密钥
	 * @return
	 */
	public static RuleResult checkSign(RuleParam rParam, String signKey){
		return checkSign(rParam.getDatas(), signKey);
	}
	
	/**
	 * 验证签名
	 * 
	 * 使用签名计算方法计算签名，再与参数集中的_sign的值进行比较
	 * 
	 * @param params 参与验证的参数
	 * @param signKey 用于验证签名的密钥
	 * @return
	 */
	public static RuleResult checkSign(Map<String, ?> params, String signKey){
		RuleResult result = RuleResult.create();
		
		Object sign = params.get(SIGNATURE_PARAM_NAME);
		if(logger.isDebugEnabled())
			logger.debug("check sign="+sign);
		if(StringKit.isEmpty(sign))
			return result.fail(SubError.createMain(SubErrorType.ISV_MISSING_PARAMETER, SIGNATURE_PARAM_NAME));
		if(!sign.equals(RuleKit.getSign(params, signKey)))
			return result.fail(SubError.createMain(SubErrorType.ISV_INVALID_PARAMETER, SIGNATURE_PARAM_NAME));
		return result.success();
	}
	
	/**
	 * 对规则返回结果进行签名
	 * 
	 * 签名结果放在RuleResult的extraData中，key为_sign
	 * 
	 * @param signKey
	 * @param result
	 */
	public static void signResult(String signKey, RuleResult result){
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", StringKit.getString(result.isSuccess()));
		resultMap.put("content", StringKit.getString(result.getContent()));
		resultMap.put("msg", StringKit.getString(result.getMsg()));
		result.putExtraData(SIGNATURE_PARAM_NAME, getSign(resultMap, signKey));
	}
	
	/**
	 * 调用无需提供用户身份的规则
	 * 
	 * @param request
	 * @param ruleURI 要调用规则的路径
	 * @param rParam 要传递的参数
	 * @return
	 */
	public static RuleResult invokeRule(HttpServletRequest request, String ruleURI, RuleParam rParam){
		return invokeRule(request, null, null, null, ruleURI, rParam, false);
	}
	
	/**
	 * 以给定用户的身份调用规则
	 * 
	 * @param request
	 * @param loginId 调用者的用户帐号
	 * @param ruleURI 要调用规则的路径
	 * @param rParam 要传递的参数
	 * @return
	 */
	public static RuleResult invokeRule(HttpServletRequest request, String loginId, String ruleURI, RuleParam rParam){
		return invokeRule(request, loginId, null, null, ruleURI, rParam, false);
	}
	
	/**
	 * 以给定群组用户的身份调用规则
	 * 
	 * @param request
	 * @param loginId 调用者的用户帐号
	 * @param companyCode 调用者的所在群组
	 * @param ruleURI 要调用规则的路径
	 * @param rParam 要传递的参数
	 * @return
	 */
	public static RuleResult invokeRule(HttpServletRequest request, String loginId, String companyCode, String ruleURI, RuleParam rParam){
		return invokeRule(request, loginId, companyCode, null, ruleURI, rParam, false);
	}
	
	/**
	 * 以给定群组用户的身份调用规则
	 * 
	 * @param request
	 * @param loginId 调用者的用户帐号
	 * @param companyCode 调用者的所在群组
	 * @param sceneKey 调用场景名
	 * @param ruleURI 要调用规则的路径
	 * @param rParam 要传递的参数
	 * @return
	 */
	public static RuleResult invokeRule(HttpServletRequest request, String loginId, String companyCode, String sceneKey, String ruleURI, RuleParam rParam){
		return invokeRule(request, loginId, companyCode, sceneKey, ruleURI, rParam, false);
	}
	
	/**
	 * 以给定场景中群组用户的身份调用规则
	 * 
	 * @param request
	 * @param loginId 调用者的用户帐号
	 * @param companyCode 调用者的所在群组
	 * @param sceneKey 调用场景名
	 * @param ruleURI 要调用规则的路径
	 * @param rParam 要传递的参数
	 * @return
	 */
	public static RuleResult invokeRule(HttpServletRequest request, String loginId, String companyCode, String sceneKey, String ruleURI, RuleParam rParam, boolean isAsyn){
		if(StringKit.isEmpty(sceneKey) && request!=null)
			sceneKey = RuleKit.getStringAttribute(request, JoyManager.getServer().getSessionSceneKeyParam());
		return JoyManager.getRuleExecutor().execute(RuleContext.create().user(loginId).company(companyCode).sceneKey(sceneKey).uri(ruleURI), rParam, isAsyn);
	}
}
