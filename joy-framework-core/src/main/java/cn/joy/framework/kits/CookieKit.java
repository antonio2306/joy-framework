package cn.joy.framework.kits;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie操作工具类
 */
public class CookieKit {
	/**
	 * 
	 * @param request
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String name, String defaultValue) {
		Cookie cookie = getCookieObject(request, name);
		return cookie != null ? cookie.getValue() : defaultValue;
	}
	
	public static String getCookie(HttpServletRequest request, String name) {
		return getCookie(request, name, null);
	}
	
	public static Cookie getCookieObject(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name))
					return cookie;
		return null;
	}
	
	public static Cookie[] getCookieObjects(HttpServletRequest request) {
		Cookie[] result = request.getCookies();
		return result != null ? result : new Cookie[0];
	}
	
	public static void setCookie(HttpServletResponse response, Cookie cookie) {
		response.addCookie(cookie);
	}
	
	public static void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds, String path) {
		setCookie(response, name, value, maxAgeInSeconds, path, null);
	}
	
	public static void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds, String path, String domain) {
		Cookie cookie = new Cookie(name, value);
		if (domain != null)
			cookie.setDomain(domain);
		cookie.setMaxAge(maxAgeInSeconds);
		cookie.setPath(path);
		response.addCookie(cookie);
	}
	
	public static void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
		setCookie(response, name, value, maxAgeInSeconds, "/", null);
	}
	
	public static void removeCookie(HttpServletResponse response, String name) {
		setCookie(response, name, null, 0, "/", null);
	}
	
	public static void removeCookie(HttpServletResponse response, String name, String path) {
		setCookie(response, name, null, 0, path, null);
	}
	
	public static void removeCookie(HttpServletResponse response, String name, String path, String domain) {
		setCookie(response, name, null, 0, path, domain);
	}
}
