package cn.joy.framework.plugin.jfinal;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;

public class BaseConfig extends JFinalConfig{
	private static Logger logger = Logger.getLogger(BaseConfig.class);

	@Override
	public void configConstant(Constants me) {
		
	}

	@Override
	public void configRoute(Routes me) {
		
	}

	@Override
	public void configPlugin(Plugins me) {
		
	}

	@Override
	public void configInterceptor(Interceptors me) {
		
	}

	@Override
	public void configHandler(Handlers me) {
		
	}

	@Override
	public void afterJFinalStart() {
		try {
			JoyManager.init();
		} catch (Exception e) {
			throw new RuntimeException("JOY start fail");
		}
	}
}
