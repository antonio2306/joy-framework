package cn.joy.framework.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.joy.framework.plugin.IPlugin;
/**
 * MVC插件接口，提供开发规则和业务规则请求路径的获取接口
 * @author liyy
 * @date 2014-07-06
 */
public interface IMVCPlugin extends IPlugin {
	public String getOpenRequestPath(HttpServletRequest request, String action, String params, Map<String, String> datas);
	
	public String getBusinessRequestPath(HttpServletRequest request, String action, String params, Map<String, String> datas);
}
