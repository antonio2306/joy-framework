package cn.joy.framework.kits;

import org.apache.commons.lang.math.NumberUtils;
/**
 * 数值操作工具类
 * @author liyy
 * @date 2014-05-20
 */
public class NumberKit {
	public static boolean isNumber(String value) {
		return NumberUtils.isNumber(value);
	}

	public static Long getLong(Object value) {
		return getLong(value, null);
	}

	public static Long getLong(Object value, Long defaultValue) {
		try {
			return new Long(value.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Double getDouble(Object value) {
		return getDouble(value, null);
	}

	public static Double getDouble(Object value, Double defaultValue) {
		try {
			return new Double(value.toString());
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static Float getFloat(Object value) {
		return getFloat(value, null);
	}

	public static Float getFloat(Object value, Float defaultValue) {
		try {
			return new Float(value.toString());
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static Integer getInteger(Object value) {
		return getInteger(value, null);
	}

	public static Integer getInteger(Object value, Integer defaultValue) {
		try {
			return new Integer(value.toString());
		} catch (Exception ex) {
			return defaultValue;
		}
	}
}
