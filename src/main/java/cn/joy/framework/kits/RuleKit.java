package cn.joy.framework.kits;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * 规则工具类
 * @author liyy
 * @date 2014-05-20
 */
public class RuleKit {
	private static Logger logger = Logger.getLogger(RuleKit.class);
	
	private static String getParam(HttpServletRequest request, String key){
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
}
