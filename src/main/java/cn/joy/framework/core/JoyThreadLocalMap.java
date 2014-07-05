package cn.joy.framework.core;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author liyy
 * @date 2014-05-21
 */
public class JoyThreadLocalMap extends ThreadLocal<Map<String, Object>>{
	public void setThreadAttribute( String key, Object obj ){
		Map<String, Object> threadLocalMap = (Map<String, Object>)this.get();
		if( threadLocalMap == null ){
			threadLocalMap = new HashMap<String, Object>();
		}
		threadLocalMap.put(key, obj);
		this.set( threadLocalMap );
	}
	
	public Object getThreadAttribute( String key ){
		Map<String, Object> threadLocalMap = (Map<String, Object>)this.get();
		if( threadLocalMap == null ){
			return null;
		}else{
			return threadLocalMap.get(key);
		}
	}
	
	public void removeThreadAttribute( String key ){
		Map<String, Object> threadLocalMap = (Map<String, Object>)this.get();
		if( threadLocalMap != null ){
			threadLocalMap.remove(key);
			this.set( threadLocalMap );
		}
	}
	
	public void clearThreadAttribute(){
		this.remove();
	}
}
