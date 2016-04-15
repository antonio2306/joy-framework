package cn.joy.framework.core;

/**
 * Fluent Style Map
 * @author raymond.li
 * @date 2015-12-06
 */
@SuppressWarnings({ "rawtypes" })
public class JoyMap<K, V> extends JoyGeneric.GenericMap<JoyMap<K, V>, K, V> {
	public static JoyMap<String, Object> createStringObject(){
		return new JoyMap<String, Object>();
	}
}
