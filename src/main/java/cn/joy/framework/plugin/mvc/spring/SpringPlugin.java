package cn.joy.framework.plugin.mvc.spring;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.mvc.IMVCPlugin;

public class SpringPlugin implements IMVCPlugin{
	public static String MVC_OPEN_REQUEST_URL = "openservice.do";
	public static String MVC_BUSINESS_REQUEST_URL = "businesservice.do";
	
	private String getRequestPath(String baseURL, String action, String params, Map<String, String> datas){
		String url = baseURL+"?action="+action+StringKit.getString(params);
		if(datas!=null){
			try {
				for(Entry<String, String> entry:datas.entrySet()){
					url += "&"+entry.getKey()+"="+URLEncoder.encode(entry.getValue(), JoyManager.getServer().getCharset());
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return url;
	}

	public String getOpenRequestPath(String action, String params, Map<String, String> datas){
		return getRequestPath(MVC_OPEN_REQUEST_URL, action, params, datas);
	}
	
	public String getBusinessRequestPath(String action, String params, Map<String, String> datas){
		return getRequestPath(MVC_BUSINESS_REQUEST_URL, action, params, datas);
	}

	public void start(){
		
	}
	
	public void stop(){
		
	}
}
