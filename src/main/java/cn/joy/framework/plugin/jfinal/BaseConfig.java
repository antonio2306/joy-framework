package cn.joy.framework.plugin.jfinal;

import java.io.IOException;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.JsonKit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.Model;

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
			throw new RuntimeException("JOY start fail", e);
		}
		
		JsonKit.addSerializer(Model.class, new JsonSerializer<Model>() {
			@Override
			public void serialize(Model value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
				jgen.writeRawValue(value.toJson());
			}

		});
	}
}
