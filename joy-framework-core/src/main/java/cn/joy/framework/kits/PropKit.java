package cn.joy.framework.kits;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PropKit {
	
	private static Prop prop = null;
	private static final Map<String, Prop> map = new ConcurrentHashMap<String, Prop>();
	
	private PropKit() {}
	
	/**
	 * 使用指定文件对应的Prop，无则从文件中读取并创建
	 * @param fileName
	 * @return
	 */
	public static Prop use(String fileName) {
		return use(fileName, Prop.encoding);
	}
	
	/**
	 * 使用指定文件和字符集编码对应的Prop，无则从文件中读取并创建
	 * @param fileName
	 * @param encoding
	 * @return
	 */
	public static Prop use(String fileName, String encoding) {
		Prop result = map.get(fileName);
		if (result == null) {
			result = new Prop(fileName, encoding);
			map.put(fileName, result);
			if (PropKit.prop == null)
				PropKit.prop = result;
		}
		return result;
	}
	
	/**
	 * 使用指定文件对应的Prop，无则从文件中读取并创建
	 * @param file
	 * @return
	 */
	public static Prop use(File file) {
		return use(file, Prop.encoding);
	}
	
	/**
	 * 使用指定文件和字符集编码对应的Prop，无则从文件中读取并创建
	 * @param file
	 * @param encoding
	 * @return
	 */
	public static Prop use(File file, String encoding) {
		Prop result = map.get(file.getName());
		if (result == null) {
			result = new Prop(file, encoding);
			map.put(file.getName(), result);
			if (PropKit.prop == null)
				PropKit.prop = result;
		}
		return result;
	}
	
	/**
	 * 移除指定文件对应的Prop
	 * @param fileName
	 * @return
	 */
	public static Prop useless(String fileName) {
		Prop previous = map.remove(fileName);
		if (PropKit.prop == previous)
			PropKit.prop = null;
		return previous;
	}
	
	/**
	 * 从输入流中创建Prop
	 * @param inputStream
	 * @return
	 */
	public static Prop use(InputStream inputStream) {
		return new Prop(inputStream);
	}
	
	/**
	 * 清空当前所有使用file key缓存的Prop
	 * @param inputStream
	 * @return
	 */
	public static void clear() {
		prop = null;
		map.clear();
	}
	
	/**
	 * 获取默认Prop
	 * @return
	 */
	public static Prop getProp() {
		if (prop == null)
			throw new IllegalStateException("Load propties file by invoking PropKit.use(String fileName) method first.");
		return prop;
	}
	
	/**
	 * 从缓存中获取给定文件对应的Prop
	 * 
	 * @param fileName
	 * @return
	 */
	public static Prop getProp(String fileName) {
		return map.get(fileName);
	}
	
	/**
	 * 从默认Prop中获取指定key对应的值
	 * @param key
	 * @return 如果没有对应的值，则返回空字符串
	 */
	public static String get(String key) {
		return getProp().get(key);
	}
	
	/**
	 * 从默认Prop中获取指定key对应的值
	 * @param key
	 * @param defaultValue
	 * @return 如果没有对应的值，则返回默认值
	 */
	public static String get(String key, String defaultValue) {
		return getProp().get(key, defaultValue);
	}
	
	/**
	 * 从默认Prop中获取指定key对应的整型值
	 * @param key
	 * @return 
	 */
	public static Integer getInt(String key) {
		return getProp().getInt(key);
	}
	
	/**
	 * 从默认Prop中获取指定key对应的整型值
	 * @param key
	 * @param defaultValue
	 * @return 如果没有对应的值，则返回默认值
	 */
	public static Integer getInt(String key, Integer defaultValue) {
		return getProp().getInt(key, defaultValue);
	}
	
	/**
	 * 从默认Prop中获取指定key对应的长整型值
	 * @param key
	 * @return 
	 */
	public static Long getLong(String key) {
		return getProp().getLong(key);
	}
	
	/**
	 * 从默认Prop中获取指定key对应的长整型值
	 * @param key
	 * @param defaultValue
	 * @return 如果没有对应的值，则返回默认值
	 */
	public static Long getLong(String key, Long defaultValue) {
		return getProp().getLong(key, defaultValue);
	}
	
	/**
	 * 从默认Prop中获取指定key对应的布尔值
	 * @param key
	 * @return 
	 */
	public static Boolean getBoolean(String key) {
		return getProp().getBoolean(key);
	}
	
	/**
	 * 从默认Prop中获取指定key对应的布尔值
	 * @param key
	 * @param defaultValue
	 * @return 如果没有对应的值，则返回默认值
	 */
	public static Boolean getBoolean(String key, Boolean defaultValue) {
		return getProp().getBoolean(key, defaultValue);
	}
	
	/**
	 * 默认Prop是否包含指定key
	 * @param key
	 * @return
	 */
	public static boolean containsKey(String key) {
		return getProp().containsKey(key);
	}
	
	/**
	 * 从默认Prop中获取按key前缀过滤的Map
	 * @param keyPrefix 指定key的前缀
	 * @return 获取符合给定前缀的所有key，使用这些key及其value构成Map
	 */
	public static Map<String, String> getMap(String keyPrefix) {
		return getProp().getMap(keyPrefix);
    }
	
	/**
	 * 从默认Prop中获取按key前缀过滤的Map
	 * @param keyPrefix
	 * @return 获取符合给定前缀的所有key，使用这些key去掉前缀后，再和其value构成Map
	 */
	public Map<String, String> getMapTrimPrefix(String keyPrefix) {
		return getProp().getMapTrimPrefix(keyPrefix);
    }
	
	/**
	 * 从默认Prop中移除指定key
	 * @param key
	 * @return
	 */
	public Prop remove(String key){
		return getProp().remove(key);
	}
	
	/**
	 * 移除默认Prop中的所有key
	 * @return
	 */
	public Prop removeAll(){
		return getProp().removeAll();
	}
	
	/**
	 * 移除默认Prop中所有符合给定前缀的key
	 * @param keyPrefix
	 * @return
	 */
	public Prop removeAll(String keyPrefix){
		return getProp().removeAll(keyPrefix);
	}
	
	/**
	 * 将key和value放入默认Prop
	 * @param key
	 * @param value
	 * @return
	 */
	public Prop set(String key, Object value){
		return getProp().set(key, value);
	}
	
	/**
	 * 将属性集中的所有key和value放入默认Prop
	 * @param prop
	 * @return
	 */
	public Prop setAll(Properties prop){
		return getProp().setAll(prop);
	}
	
	/**
	 * 将指定Prop中的所有key和value放入默认Prop
	 * @param prop
	 * @return
	 */
	public Prop setAll(Prop prop){
		return getProp().setAll(prop);
	}
}

