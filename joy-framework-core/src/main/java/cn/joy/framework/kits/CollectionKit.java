package cn.joy.framework.kits;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CollectionKit {
	/**
     * 转置 Map
     */
    public static <K, V> Map<V, K> invert(Map<K, V> source) {
        Map<V, K> target = null;
        if (source!=null && !source.isEmpty()) {
            target = new LinkedHashMap<V, K>(source.size());
            for (Map.Entry<K, V> entry : source.entrySet()) {
                target.put(entry.getValue(), entry.getKey());
            }
        }
        return target;
    }
    
    //并不保证可转换性，仅为了书写方便
    public static <K> Set<K> convertSetType(Set<?> set, Class<K> toClass){
    	Set<K> newSet = new HashSet<>();
    	for(Object element:set){
    		newSet.add((K)element);
    	}
    	return newSet;
    }
    
}
