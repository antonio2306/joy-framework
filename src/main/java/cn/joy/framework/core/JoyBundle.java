package cn.joy.framework.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.joy.framework.kits.StringKit;
/**
 * 仿Android的Bundle，但对数据类型限制降低
 * @author liyy
 * @date 2014-05-20
 */
public abstract class JoyBundle<T> extends JoyMap<T, String, Object> {
	public Map<String, Object> getDatas(){
		return mMap;
	}
	
	public Map<String, Object> getDatas(String keyPrefix){
		if(StringKit.isEmpty(keyPrefix))
			return mMap;
		
		Map<String, Object> subMap = new HashMap<String, Object>();
		Set<String> keys = mMap.keySet();
		for(String key:keys){
			if(key.startsWith(keyPrefix))
				subMap.put(key, mMap.get(key));
		}
		return subMap;
	}
	
	public T removeDatas(String keyPrefix){
		if(StringKit.isNotEmpty(keyPrefix)){
			Set<String> keys = mMap.keySet();
			Iterator<String> ite = keys.iterator();
			while(ite.hasNext()){
				String key = ite.next();
				if(key.startsWith(keyPrefix))
					mMap.remove(key);
			}
		}
		return (T)this;
	}
	
	public T putAll(Map map){
		if(map!=null){
			Set keys = map.keySet();
			for(Object key:keys){
				if(key==null || key.toString().trim().length()==0)
					continue;
				mMap.put(key.toString(), map.get(key));
			}
		}
		return (T)this;
	}
	
	public T putBoolean(String key, Boolean value) {
        mMap.put(key, value);
        return (T)this;
    }

    public T putInt(String key, Integer value) {
        mMap.put(key, value);
        return (T)this;
    }

    public T putLong(String key, Long value) {
        mMap.put(key, value);
        return (T)this;
    }

    public T putFloat(String key, Float value) {
        mMap.put(key, value);
        return (T)this;
    }

    public T putDouble(String key, Double value) {
        mMap.put(key, value);
        return (T)this;
    }

    public T putString(String key, String value) {
        mMap.put(key, value);
        return (T)this;
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return Boolean.valueOf(o.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Integer getInt(String key) {
		return getInt(key, null);
	}

	public Integer getInt(String key, Integer defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Long getLong(String key) {
		return getLong(key, null);
	}

	public Long getLong(String key, Long defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(o.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Float getFloat(String key) {
		return getFloat(key, null);
	}

	public Float getFloat(String key, Float defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(o.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	public Double getDouble(String key, Double defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(o.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String getString(String key) {
		Object o = mMap.get(key);
		if (o == null) {
			return null;
		}
		try {
			return o.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public String getString(String key, String defaultValue) {
		Object o = mMap.get(key);
		if (o == null) {
			return defaultValue;
		}
		try {
			return o.toString();
		} catch (Exception e) {
			return defaultValue;
		}
	}

}
