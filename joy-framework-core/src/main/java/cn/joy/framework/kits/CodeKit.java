package cn.joy.framework.kits;

import java.util.UUID;

public class CodeKit {
	/**
	 * 获取编码
	 * 格式：UUID替换所有中划线并转为大写
	 */
	public static String getCode(){
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	
	/**
	 * 获取编码，指定前缀
	 * 格式：前缀-UUID替换所有中划线并转为大写
	 * @param prefix 前缀，建议为大写字母
	 */
	public static String getCode(String prefix){
		return prefix+"-"+getCode();
	}
	
	/**
	 * 获取编码，指定前缀和截取位数
	 * 格式：前缀-UUID替换所有中划线并转为大写然后从头截取指定位数
	 * @param prefix 前缀，建议为大写字母
	 * @param digit 截取位数，不大于31
	 */
	public static String getCode(String prefix, int digit){
		return prefix+"-"+getCode().substring(Math.min(digit, 31));
	}
	
	/**
	 * 获取指定位数的随机数字编码（每一位取值0-9）
	 * @param digit 位数
	 */
	public static String getNumberCode(int digit) {
		String result="";
		for(int i=0;i<digit;i++) {
			 result+= String.valueOf((int)(10*(Math.random())));
	    }
		return result;
	}

	/**
	 * 获取包含时间戳和随机数字的编码
	 * 当前时间毫秒数，拼接指定位数的随机数字（每一位取值0-9）
	 * @param digit 位数
	 */
	public static String generateNumberCodeWithTimestamp(int digit) {
		return System.currentTimeMillis()+getNumberCode(digit);
	}
}
