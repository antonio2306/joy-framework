package cn.joy.framework.kits;

import java.util.Collection;

import org.apache.commons.lang.CharUtils;
/**
 * 字符串操作工具类
 * @author liyy
 * @date 2014-05-20
 */
public class StringKit {
	public static boolean isEmpty(Object info) {
		if (info == null)
			return true;
		return info.toString().trim().length() == 0;
	}

	public static boolean isNotEmpty(Object info) {
		if (info == null)
			return false;
		return info.toString().trim().length() > 0;
	}

	public static String getString(Object s) {
		if (s == null)
			return "";
		return s.toString().trim();
	}
	
	public static String getString(Object s, String defaultValue) {
		if (isEmpty(s))
			return defaultValue;
		return s.toString().trim();
	}

	public static String capitalize(String s){
		 return new StringBuilder().append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
	}
	
	public static int getOccurIndexOf(String src, String find, int times) {
		if (src == null || src.length() == 0)
			return -1;
		int o = 0;
		int index = -1;
		while ((index = src.indexOf(find, index)) > -1) {
			++index;
			++o;
			if (o == times) 
				return index - 1;
		}
		return -1;
	}
	
	public static String joinCollection(Collection c, String delimiter){
		String result = "";
		StringBuilder s = new StringBuilder();
		if(c!=null){
			for(Object o:c){
				s.append(o).append(delimiter);
			}
			result = s.toString();
			if(result.endsWith(delimiter))
				result = result.substring(0, result.lastIndexOf(delimiter));
		}
		return result;
	}
	
	public static boolean contains4ByteChar(String source) throws Exception{
		byte[] t1 = source.getBytes("UTF-8");  
        for (int i = 0; i < t1.length;) {  
            byte tt = t1[i];  
            if (CharUtils.isAscii((char) tt)) {  
                byte[] ba = new byte[1];  
                ba[0] = tt;  
                i++;  
            }  
            if ((tt & 0xE0) == 0xC0) {  
                byte[] ba = new byte[2];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                i++;  
                i++;  
            }  
            if ((tt & 0xF0) == 0xE0) {  
                byte[] ba = new byte[3];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                ba[2] = t1[i+2];  
                i++;  
                i++;  
                i++;  
            }  
            if ((tt & 0xF8) == 0xF0) {  
                return true;
            }  
        } 
        return false;
	}
	
	public static String filter4ByteChar(String source) throws Exception{
		StringBuilder s = new StringBuilder();
		byte[] t1 = source.getBytes("UTF-8");  
        for (int i = 0; i < t1.length;) {  
            byte tt = t1[i];  
            if (CharUtils.isAscii((char) tt)) {  
                byte[] ba = new byte[1];  
                ba[0] = tt;  
                i++;  
                String result = new String(ba, "UTF-8");  
                //System.out.println("1个字节的字符:" + result);  
                s.append(result);
            }  
            if ((tt & 0xE0) == 0xC0) {  
                byte[] ba = new byte[2];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                i++;  
                i++;  
                String result = new String(ba, "UTF-8");  
                //System.out.println("2个字节的字符:" + result);  
                s.append(result);
            }  
            if ((tt & 0xF0) == 0xE0) {  
                byte[] ba = new byte[3];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                ba[2] = t1[i+2];  
                i++;  
                i++;  
                i++;  
                String result = new String(ba, "UTF-8");  
                //System.out.println("3个字节的字符:" + result);  
                s.append(result);
            }  
            if ((tt & 0xF8) == 0xF0) {  
                byte[] ba = new byte[4];  
                ba[0] = tt;  
                ba[1] = t1[i+1];  
                ba[2] = t1[i+2];  
                ba[3] = t1[i+3];  
                i++;  
                i++;  
                i++;  
                i++;  
                String result = new String(ba, "UTF-8");  
                //System.out.println("4个字节的字符:" + result);  
                s.append("(-)");
            }  
        }
        return s.toString();
	}
	
}
