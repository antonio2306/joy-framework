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

public class Prop {
	public static final String encoding = "UTF-8";
	private Properties properties = null;
	
	Prop(String fileName) {
		this(fileName, encoding);
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
	
	public String get(String key) {
		return StringKit.getString(properties.getProperty(key));
	}
	
	public String get(String key, String defaultValue) {
		return StringKit.getString(properties.getProperty(key), defaultValue);
	}
	
	public Integer getInt(String key) {
		return NumberKit.getInteger(properties.getProperty(key));
	}
	
	public Integer getInt(String key, Integer defaultValue) {
		return NumberKit.getInteger(properties.getProperty(key), defaultValue);
	}
	
	public Long getLong(String key) {
		return NumberKit.getLong(properties.getProperty(key));
	}
	
	public Long getLong(String key, Long defaultValue) {
		return NumberKit.getLong(properties.getProperty(key), defaultValue);
	}
	
	public Boolean getBoolean(String key) {
		String value = get(key);
		return (value != null) ? Boolean.parseBoolean(value) : null;
	}
	
	public Boolean getBoolean(String key, Boolean defaultValue) {
		String value = get(key);
		return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
	}
	
	public Map<String, Object> getMap(String prefix) {
        Map<String, Object> kvMap = new LinkedHashMap<String, Object>();
        Set<String> keySet = properties.stringPropertyNames();
        for (String key : keySet) {
            if (key.startsWith(prefix)) {
                String value = properties.getProperty(key);
                kvMap.put(key, value);
            }
        }
        return kvMap;
    }
	
	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}
	
	public Properties getProperties() {
		return properties;
	}
}
