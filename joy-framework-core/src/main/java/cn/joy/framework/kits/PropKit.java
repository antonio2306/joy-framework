package cn.joy.framework.kits;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.joy.framework.core.JoyMap;
import cn.joy.framework.kits.LogKit.Log;

/**
 * 属性集操作工具类
 */
public class PropKit {
	private static Log logger = LogKit.getLog(PropKit.class);
	private static Prop prop = null;
	private static final Map<String, Prop> map = new ConcurrentHashMap<String, Prop>();
	
	private PropKit() {}
	
	/**
	 * 创建一个空的Prop
	 * @return
	 */
	public static Prop empty() {
		return new Prop();
	}
	
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
	 * 从默认Prop中获取按key前缀过滤的子Prop
	 * @param keyPrefix 指定key的前缀
	 * @return 获取符合给定前缀的所有key，使用这些key及其value构成Prop
	 */
	public static Prop getSubProp(String keyPrefix) {
		return getProp().getSubProp(keyPrefix);
    }
	
	/**
	 * 从默认Prop中获取按key前缀过滤的子Prop
	 * @param keyPrefix
	 * @return 获取符合给定前缀的所有key，使用这些key去掉前缀后，再和其value构成Prop
	 */
	public Prop getSubPropTrimPrefix(String keyPrefix) {
		return getProp().getSubPropTrimPrefix(keyPrefix);
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
	
	/**
	 * 判断默认Prop中的属性集
	 * @return
	 */
	public Properties getProperties(){
		return getProp().getProperties();
	}
	
	/**
	 * 判断默认Prop是否为空
	 * @return
	 */
	public boolean isEmpty() {
		return getProp().isEmpty();
	}
	
	public static class Prop {
		public static final String encoding = "UTF-8";
		private Properties properties = null;
		
		Prop(){
			properties = new Properties();
		}
		
		Prop(String fileName) {
			this(fileName, encoding);
		}
		
		public Prop(InputStream inputStream) {
			this(inputStream, encoding);
		}
		
		public Prop(JoyMap<String, Object> props) {
			properties = new Properties();
			properties.putAll(props.map());
		}
		
		public Prop(Map<String, Object> props) {
			properties = new Properties();
			properties.putAll(props);
		}
		
		Prop(String fileName, String encoding) {
			InputStream inputStream = null;
			try {
				inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);		// properties.load(Prop.class.getResourceAsStream(fileName));
				if (inputStream == null)
					throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
				properties = new Properties();
				properties.load(new InputStreamReader(inputStream, encoding));
				logger.debug("load Prop from {}: {}", fileName, properties);
			} catch (Exception e) {
				throw new RuntimeException("Error loading properties file.", e);
			}
			finally {
				if (inputStream != null) try {inputStream.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
		
		Prop(File file) {
			this(file, encoding);
		}
		
		Prop(File file, String encoding) {
			if (file == null)
				throw new IllegalArgumentException("File can not be null.");
			if (file.isFile() == false)
				throw new IllegalArgumentException("Not a file : " + file.getName());
			
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				properties = new Properties();
				properties.load(new InputStreamReader(inputStream, encoding));
				logger.debug("load Prop from {}: {}", file.getName(), properties);
			} catch (IOException e) {
				throw new RuntimeException("Error loading properties file.", e);
			}
			finally {
				if (inputStream != null) try {inputStream.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
		
		Prop(InputStream inputStream, String encoding) {
			try {
				properties = new Properties();
				properties.load(new InputStreamReader(inputStream, encoding));
				logger.debug("load Prop from stream: {}", properties);
			} catch (IOException e) {
				throw new RuntimeException("Error loading properties stream.", e);
			}
			finally {
				if (inputStream != null) try {inputStream.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
		
		public String get(String key) {
			return StringKit.getString(properties.getProperty(key));
		}
		
		public String get(String key, String defaultValue) {
			return StringKit.getString(properties.getProperty(key), defaultValue);
		}
		
		public Integer getInt(String key) {
			return TypeKit.toInt(properties.getProperty(key));
		}
		
		public Integer getInt(String key, Integer defaultValue) {
			return TypeKit.toInt(properties.getProperty(key), defaultValue);
		}
		
		public Long getLong(String key) {
			return TypeKit.toLong(properties.getProperty(key));
		}
		
		public Long getLong(String key, Long defaultValue) {
			return TypeKit.toLong(properties.getProperty(key), defaultValue);
		}
		
		public Boolean getBoolean(String key) {
			return TypeKit.toBoolean(properties.getProperty(key));
		}
		
		public Boolean getBoolean(String key, Boolean defaultValue) {
			return TypeKit.toBoolean(properties.getProperty(key), defaultValue);
		}
		
		public Prop getSubProp(String prefix) {
			Prop subProp = new Prop();
	        Set<String> keySet = properties.stringPropertyNames();
	        for (String key : keySet) {
	            if (key.startsWith(prefix)) {
	                String value = properties.getProperty(key);
	                subProp.set(key, value);
	            }
	        }
	        return subProp;
	    }
		
		public Prop getSubPropTrimPrefix(String prefix) {
			Prop subProp = new Prop();
	        Set<String> keySet = properties.stringPropertyNames();
	        for (String key : keySet) {
	            if (key.startsWith(prefix)) {
	                String value = properties.getProperty(key);
	                subProp.set(key.substring(prefix.length()), value);
	            }
	        }
	        return subProp;
	    }
		
		public Prop remove(String key){
			properties.remove(key);
			return this;
		}
		
		public Prop removeAll(){
			properties.clear();
			return this;
		}
		
		public Prop removeAll(String keyPrefix){
			for(String key:properties.stringPropertyNames()){
				if(key.startsWith(keyPrefix))
					properties.remove(key);
			}
			return this;
		}
		
		public Prop set(String key, Object value){
			properties.put(key, value);
			return this;
		}
		
		public Prop setAll(Properties prop){
			properties.putAll(prop);
			return this;
		}
		
		public Prop setAll(Prop prop){
			properties.putAll(prop.getProperties());
			return this;
		}
		
		public boolean containsKey(String key) {
			return properties.containsKey(key);
		}
		
		public Set<String> keys(){
			return properties.stringPropertyNames();
		}
		
		public Properties getProperties() {
			return properties;
		}
		
		public boolean isEmpty() {
			return properties.isEmpty();
		}
		
		public Map<String, Object> toMap(){
			Map<String, Object> map = new HashMap<>();
			Set<String> keySet = properties.stringPropertyNames();
	        for (String key : keySet) {
                String value = properties.getProperty(key);
                map.put(key, value);
	        }
	        return map;
		}
		
		@Override
		public String toString(){
			return properties.toString();
		}
	}
}

