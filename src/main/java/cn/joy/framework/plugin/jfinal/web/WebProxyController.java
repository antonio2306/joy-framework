package cn.joy.framework.plugin.jfinal.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.HttpKit;
import cn.joy.framework.kits.RuleKit;
import cn.joy.framework.server.RouteManager;

public class WebProxyController extends BusinessRuleController {
	private Logger logger = Logger.getLogger(WebProxyController.class);

	public void index(){
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
	
		String serverCode = RuleKit.getStringParam(request, "serverCode");
		//String service = RuleKit.getStringParam(request, "_s");
		//String action = RuleKit.getStringParam(request, "_m");
		
		Map<String, Object> datas = new HashMap<String, Object>();
		Map<String, String[]> params = request.getParameterMap();
		for(Entry<String, String[]> entry:params.entrySet()){
			datas.put(entry.getKey(), entry.getValue()[0]);
		}
		
		String serverURL = RouteManager.getServerURLByCompanyCode(serverCode);
		String currentServerURL = RouteManager.getServerURLByTag(RouteManager.getLocalServerTag());
		if(currentServerURL.equals(serverURL)){
			super.index();
		}else{
			String url = RouteManager.getServerURLByCompanyCode(serverCode)+"/"+JoyManager.getMVCPlugin().getBusinessRequestPath(request, "", "", null);
			if(logger.isDebugEnabled())
				logger.debug("web proxy url="+url+", datas="+datas);
			HttpKit.writeResponse(response, HttpKit.post(url, datas));
		}
		renderNull();
	}
}
