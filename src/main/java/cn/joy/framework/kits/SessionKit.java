package cn.joy.framework.kits;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionKit {
	public static Object get(HttpServletRequest request, String key){
		if(request==null)
			return null;
		HttpSession session = request.getSession();
		return session==null?null:session.getAttribute(key);
	}
	
	public static void set(HttpServletRequest request, String key, Object value){
		if(request==null)	return;
		HttpSession session = request.getSession();
		if(session!=null)
			session.setAttribute(key, value);
	}
	
	public static Object get(String sessionId, String key){
		return null;
	}
	
	public static void set(String sessionId, String key, Object value){
		
	}
}
