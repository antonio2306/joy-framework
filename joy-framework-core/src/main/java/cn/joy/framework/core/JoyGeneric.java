package cn.joy.framework.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.joy.framework.kits.StringKit;

/**
 * @author raymond.li
 * @date 2015-12-06
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class JoyGeneric {
	public static class GenericMap<T extends GenericMap, K, V> {
		protected Map<K, V> mMap = new HashMap<>();

		public T put(K key, V value) {
			mMap.put(key, value);
			return (T)this;
		}

		public V get(K key) {
			return mMap.get(key);
		}
		
		public T remove(K key) {
			mMap.remove(key);
			return (T)this;
		}
		
		public T put(Map<? extends K, ? extends V> m){
			mMap.putAll(m);
			return (T)this;
		}
		
		public T put(T joyMap){
			mMap.putAll(joyMap.map());
			return (T)this;
		}
		
		public T putNotEmpty(K key, V value) {
	        if (StringKit.isNotEmpty(value)) {
	        	mMap.put(key, value);
	        }
	        return (T)this;
	    }

	    public T putNotNull(K key, V value) {
	        if (value != null) {
	        	mMap.put(key, value);
	        }
	        return (T)this;
	    }

	    public T putWhen(boolean when, K key, V value) {
	        if (when) {
	        	mMap.put(key, value);
	        }
	        return (T)this;
	    }
		
		public boolean containsKey(K key){
			return mMap.containsKey(key);
		}
		
		public boolean containsKeys(K... keys){
			for(K key:keys){
				if(!mMap.containsKey(key))
					return false;
			}
			return true;
		}
		
		public boolean containsValue(V value){
			return mMap.containsValue(value);
		}
		
		public int size(){
			return mMap.size();
		}
		
		public boolean isEmpty(){
			return mMap.size()==0;
		}
		
		public boolean isNotEmpty(){
			return mMap.size()>0;
		}
		
		public T clear(){
			mMap.clear();
			return (T)this;
		}
		
		public Map<K, V> map(){
			return mMap;
		}
		
		public Set<Entry<K, V>> entry(){
			return mMap.entrySet();
		}
		
		public Set<K> keys(){
			return mMap.keySet();
		}
		
		public Collection<V> values(){
			return mMap.values();
		}
		
		@Override
		public String toString() {
			return mMap.toString();
		}
		
		@Override
		public int hashCode() {
			return mMap.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof GenericMap))
	            return false;
			GenericMap jMap = (GenericMap)obj;
			return mMap.equals(jMap.map());
		}
		
	    public boolean getBoolean(K key, boolean defaultValue) {
			Object o = mMap.get(key);
			if (o == null) {
				return defaultValue;
			}
			return Boolean.parseBoolean(o.toString());
		}

		public Integer getInt(K key) {
			return getInt(key, null);
		}

		public Integer getInt(K key, Integer defaultValue) {
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

		public Long getLong(K key) {
			return getLong(key, null);
		}

		public Long getLong(K key, Long defaultValue) {
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

		public Float getFloat(K key) {
			return getFloat(key, null);
		}

		public Float getFloat(K key, Float defaultValue) {
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

		public Double getDouble(K key) {
			return getDouble(key, null);
		}

		public Double getDouble(K key, Double defaultValue) {
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
		
		public BigInteger getBigInteger(K key) {
			return getBigInteger(key, null);
		}

		public BigInteger getBigInteger(K key, BigInteger defaultValue) {
			Object o = mMap.get(key);
			if (o == null) {
				return defaultValue;
			}
			try {
				return new BigInteger(o.toString());
			} catch (Exception e) {
				return defaultValue;
			}
		}
		
		public BigDecimal getBigDecimal(K key) {
			return getBigDecimal(key, null);
		}

		public BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
			Object o = mMap.get(key);
			if (o == null) {
				return defaultValue;
			}
			try {
				return new BigDecimal(o.toString());
			} catch (Exception e) {
				return defaultValue;
			}
		}

		public String getString(K key) {
			return getString(key, null);
		}

		public String getString(K key, String defaultValue) {
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
	
	public static class GenericList<T extends GenericList, V> implements Iterable<V>{
		protected List<V> mList = new ArrayList<V>();
		
		public T add(V... items) {
			if (items != null){
				for(V item:items){
					mList.add(item);
				}
			}
			return (T)this;
		}
		
		public T addNotEmpty(V... items){
			if (items != null){
				for(V item:items){
					if(StringKit.isNotEmpty(item))
						mList.add(item);
				}
			}
			return (T)this;
		}
		
		public T addNotNull(V... items){
			if (items != null){
				for(V item:items){
					if(item!=null)
						mList.add(item);
				}
			}
			return (T)this;
		}
		
		public T addWhen(boolean when, V... items) {
			if (when){
				if (items != null){
					for(V item:items){
						if(item!=null)
							mList.add(item);
					}
				}
			}
			return (T)this;
		}
		
		public T add(T list) {
			if (list != null)
				mList.addAll(list.list());
			return (T)this;
		}
		
		public V get(int index) {
			return mList.get(index);
		}
		
		public int size() {
			return mList.size();
		}
		
		public boolean isEmpty(){
			return mList.size()==0;
		}
		
		public boolean isNotEmpty(){
			return mList.size()>0;
		}
		
		public T clear(){
			mList.clear();
			return (T)this;
		}
		
		public List<V> list() {
			return mList;
		}
		
		public String[] toStringArray() {
			String[] arr = new String[mList.size()];
			for(int i=0;i<mList.size();i++){
				arr[i] = StringKit.getString(mList.get(i));
			}
			return arr;
		}
		
		public T remove(V item){
			mList.remove(item);
			return (T)this;
		}
		
		public T removeByClass(Class itemClass){
			Iterator<V> ite = this.iterator();
			while(ite.hasNext()){
				V item = ite.next();
				if(itemClass.isAssignableFrom(item.getClass()))
					ite.remove();
			}
			return (T)this;
		}

		@Override
		public Iterator<V> iterator() {
			return mList.iterator();
		}

		@Override
		public String toString() {
			return mList.toString();
		}
		
		@Override
		public int hashCode() {
			return mList.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof GenericList))
	            return false;
			GenericList jList = (GenericList)obj;
			return mList.equals(jList.list());
		}
	}
	
	public static class GenericSet<T extends GenericSet, V> implements Iterable<V>{
		protected Set<V> mSet = new HashSet<V>();
		
		public T add(V... items) {
			if (items != null){
				for(V item:items){
					mSet.add(item);
				}
			}
			return (T)this;
		}
		
		public T add(T set) {
			if (set != null)
				mSet.addAll(set.set());
			return (T)this;
		}
		
		public boolean contains(V item){
			return mSet.contains(item);
		}
		
		public int size() {
			return mSet.size();
		}
		
		public boolean isEmpty(){
			return mSet.size()==0;
		}
		
		public boolean isNotEmpty(){
			return mSet.size()>0;
		}
		
		public T clear(){
			mSet.clear();
			return (T)this;
		}
		
		public Set<V> set() {
			return mSet;
		}
		
		@Override
		public Iterator<V> iterator() {
			return mSet.iterator();
		}

		@Override
		public String toString() {
			return mSet.toString();
		}
		
		@Override
		public int hashCode() {
			return mSet.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof GenericSet))
	            return false;
			GenericSet jSet = (GenericSet)obj;
			return mSet.equals(jSet.set());
		}
	}
}
