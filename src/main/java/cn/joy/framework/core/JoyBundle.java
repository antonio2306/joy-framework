package cn.joy.framework.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.joy.framework.kits.StringKit;
/**
 * @author raymond.li
 * @date 2015-12-06
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class JoyBundle<T extends JoyBundle> extends JoyGeneric.GenericMap<T, String, Object>{
	public Map<String, Object> getDatas(){
		return this.map();
	}
	
	public Map<String, Object> getDatas(String keyPrefix){
		if(StringKit.isEmpty(keyPrefix))
			return this.map();
		
		Map<String, Object> subMap = new HashMap<String, Object>();
		Set<String> keys = this.keys();
		for(String key:keys){
			if(key.startsWith(keyPrefix))
				subMap.put(key, this.get(key));
		}
		return subMap;
	}
	
	public T removeDatas(String keyPrefix){
		if(StringKit.isEmpty(keyPrefix)){
			Set<String> keys = this.keys();
			Iterator<String> ite = keys.iterator();
			while(ite.hasNext()){
				String key = ite.next();
				if(key.startsWith(keyPrefix))
					ite.remove();
			}
		}
		return (T)this;
	}

}
