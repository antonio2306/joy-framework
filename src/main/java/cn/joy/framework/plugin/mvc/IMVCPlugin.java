package cn.joy.framework.plugin.mvc;

import java.util.Map;

import cn.joy.framework.plugin.IPlugin;

public interface IMVCPlugin extends IPlugin {
	public String getOpenRequestPath(String action, String params, Map<String, String> datas);
	
	public String getBusinessRequestPath(String action, String params, Map<String, String> datas);
}
