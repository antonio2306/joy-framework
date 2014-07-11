package cn.joy.framework.kits;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cn.joy.framework.core.JoyManager;
/**
 * 国际化工具类
 * @author liyy
 * @date 2014-05-20
 */
public class I18NKit {
	private static String baseName = "rule_errors";		//如：classes/rule_errors_zh_CN.properties
	private static Locale defaultLocale = JoyManager.getServer().getLocale();
	private static final NullResourceBundle NULL_RESOURCE_BUNDLE = new NullResourceBundle();
	private static final ConcurrentMap<String, ResourceBundle> bundlesMap = new ConcurrentHashMap<String, ResourceBundle>();

	private static volatile I18NKit me;

	private I18NKit() {
	}

	public static I18NKit me() {
		if (me == null)
			synchronized (I18NKit.class) {
				if (me == null)
					me = new I18NKit();
			}
		return me;
	}

	public static void init(String baseName, Locale defaultLocale) {
		I18NKit.baseName = baseName;
		if (defaultLocale != null)
			I18NKit.defaultLocale = defaultLocale;
	}

	public static Locale getDefaultLocale() {
		return defaultLocale;
	}

	private static ResourceBundle getResourceBundle(Locale locale) {
		String resourceBundleKey = getresourceBundleKey(locale);
		ResourceBundle resourceBundle = bundlesMap.get(resourceBundleKey);
		if (resourceBundle == null) {
			try {
				resourceBundle = ResourceBundle.getBundle(baseName, locale);
				bundlesMap.put(resourceBundleKey, resourceBundle);
			} catch (MissingResourceException e) {
				resourceBundle = NULL_RESOURCE_BUNDLE;
			}
		}
		return resourceBundle;
	}

	private static String getresourceBundleKey(Locale locale) {
		return baseName + locale.toString();
	}

	public static String getText(String key, Object... params) {
		return getText(key, null, defaultLocale, params);
	}

	public static String getText(String key, String defaultValue, Object... params) {
		return getText(key, defaultValue, defaultLocale, params);
	}

	public static String getText(String key, Locale locale, Object... params) {
		return getText(key, null, locale, params);
	}

	public static String getText(String key, String defaultValue, Locale locale, Object... params) {
		String result = null;
		try {
			result = getResourceBundle(locale).getString(key);
		} catch (Exception e) {
		}
		if(result==null)
			result = defaultValue;
		if(result!=null && params!=null){
			return String.format(result, params);
		}
		return result;
	}

	public static Locale localeFromString(String localeStr) {
		if ((localeStr == null) || (localeStr.trim().length() == 0) || ("_".equals(localeStr))) {
			return defaultLocale;
		}

		int index = localeStr.indexOf('_');
		if (index < 0) {
			return new Locale(localeStr);
		}

		String language = localeStr.substring(0, index);
		if (index == localeStr.length()) {
			return new Locale(language);
		}

		localeStr = localeStr.substring(index + 1);
		index = localeStr.indexOf('_');
		if (index < 0) {
			return new Locale(language, localeStr);
		}

		String country = localeStr.substring(0, index);
		if (index == localeStr.length()) {
			return new Locale(language, country);
		}

		localeStr = localeStr.substring(index + 1);
		return new Locale(language, country, localeStr);
	}

	private static class NullResourceBundle extends ResourceBundle {
		public Enumeration<String> getKeys() {
			return null; // dummy
		}

		protected Object handleGetObject(String key) {
			return null; // dummy
		}
		
		
	}

	public static void main(String[] args) {
		// Locale.getDefault();
		// Locale en = Locale.US;
		// Locale us = Locale.US;
		// System.out.println(l.toString());
		// System.out.println(en == us);
		// System.out.println(en.equals(us));

		// language不能唯一确定Local对象
		System.out.println(Locale.CHINESE.getLanguage());
		System.out.println(Locale.CHINA.getLanguage());
		System.out.println(Locale.SIMPLIFIED_CHINESE.getLanguage());
		System.out.println(Locale.TRADITIONAL_CHINESE.getLanguage());
		System.out.println(Locale.TAIWAN.getLanguage());

		Locale shoudong = new Locale("en");
		System.out.println(shoudong.getLanguage().equals(Locale.US.getLanguage()));
		System.out.println(shoudong.getLanguage().equals(Locale.ENGLISH.getLanguage()));
		System.out.println(shoudong.getLanguage().equals(Locale.CANADA.getLanguage()));
		System.out.println(shoudong.getLanguage().equals(Locale.UK.getLanguage()));
		System.out.println(shoudong.getLanguage().equals(Locale.CANADA_FRENCH.getLanguage()));
	}
}
