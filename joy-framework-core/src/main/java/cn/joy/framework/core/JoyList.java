package cn.joy.framework.core;

import java.util.Collections;
import java.util.Comparator;

/**
 * Fluent Style List
 * @author raymond.li
 * @date 2015-12-06
 */
public class JoyList<V> extends JoyGeneric.GenericList<JoyList<V>, V> {
	private Comparator<V> comparator;
	
	public JoyList<V> setComparator(Comparator<V> comparator){
		this.comparator = comparator;
		return this;
	}
	
	public JoyList<V> sort(){
		if(comparator==null){
			comparator = new Comparator<V>() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public int compare(V o1, V o2) {
					if(o1 instanceof Comparable && o2 instanceof Comparable)
						return ((Comparable)o1).compareTo((Comparable)o2);
					return 0;
				}
			};
		}
		
		return sort(comparator);
	}
	
	public JoyList<V> sort(Comparator<V> comparator){
		Collections.sort(this.list(), comparator);
		return this;
	}
}
