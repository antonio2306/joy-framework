package cn.joy.framework.core;

import java.util.HashMap;
import java.util.Map;
/**
 * 扩展Map，支持链式操作
 * @author liyy
 * @date 2014-05-20
 */
public abstract class JoyMap<T, K, V> {
	protected Map<K, V> mMap = new HashMap<K, V>();

	public T put(K key, V value) {
		mMap.put(key, value);
		return (T) this;
	}

	public V get(K key) {
		return mMap.get(key);
	}
	
	public T remove(K key) {
		mMap.remove(key);
		return (T) this;
	}
	
	public T clear(){
		mMap.clear();
		return (T) this;
	}
	
	@Override
	public String toString() {
		return mMap.toString();
	}
}
