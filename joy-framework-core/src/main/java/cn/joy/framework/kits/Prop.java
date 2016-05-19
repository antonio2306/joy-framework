package cn.joy.framework.kits;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.joy.framework.core.JoyMap;

public class Prop {
	public static final String encoding = "UTF-8";
	private Properties properties = null;
	
	Prop(String fileName) {
		this(fileName, encoding);
	}
	
	public Prop(){
		properties = new Properties();
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
	
	public Map<String, String> getMap(String prefix) {
        Map<String, String> kvMap = new LinkedHashMap<>();
        Set<String> keySet = properties.stringPropertyNames();
        for (String key : keySet) {
            if (key.startsWith(prefix)) {
                String value = properties.getProperty(key);
                kvMap.put(key, value);
            }
        }
        return kvMap;
    }
	
	public Map<String, String> getMapTrimPrefix(String prefix) {
        Map<String, String> kvMap = new LinkedHashMap<>();
        Set<String> keySet = properties.stringPropertyNames();
        for (String key : keySet) {
            if (key.startsWith(prefix)) {
                String value = properties.getProperty(key);
                kvMap.put(key.substring(prefix.length()), value);
            }
        }
        return kvMap;
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
	
	public Properties getProperties() {
		return properties;
	}
}
