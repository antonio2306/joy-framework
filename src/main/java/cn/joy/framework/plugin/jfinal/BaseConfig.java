package cn.joy.framework.plugin.jfinal;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

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
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.ClassKit;
import cn.joy.framework.kits.JsonKit;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.jfinal.annotation.Ctrl;
import cn.joy.framework.plugin.jfinal.annotation.Table;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BaseConfig extends JFinalConfig{
	private static Logger logger = Logger.getLogger(BaseConfig.class);

	@Override
	public void configConstant(Constants me) {
		
	}

	@Override
	public void configRoute(Routes me) {
		List<Class> controllerClasses  = ClassKit.listClassByAnnotation(this.getClass().getPackage().getName(), Ctrl.class);
		if(controllerClasses!=null){
			for(Class controllerClass:controllerClasses){
				Ctrl controllerInfo = (Ctrl)controllerClass.getAnnotation(Ctrl.class);
				if(logger.isDebugEnabled())
					logger.debug("add controller, url="+controllerInfo.url()+", viewPath="+controllerInfo.viewPath());
				String viewPath = controllerInfo.viewPath();
				if(StringKit.isEmpty(viewPath)){
					String packageName = controllerClass.getPackage().getName();
					viewPath = StringKit.matchOne(packageName, "\\.module\\.(.+)\\.web").replaceAll("\\.", "/");
				}
				
				if(StringKit.isEmpty(viewPath))
					me.add(controllerInfo.url(), controllerClass);
				else
					me.add(controllerInfo.url(), controllerClass, viewPath);
			}
		}
	}

	@Override
	public void configPlugin(Plugins me) {
		
	}
	
	protected void configTable(ActiveRecordPlugin arp, String db){
		List<Class> tableClasses  = ClassKit.listClassByAnnotation(this.getClass().getPackage().getName(), Table.class);
		if(tableClasses!=null){
			for(Class tableClass:tableClasses){
				Table tableInfo = (Table)tableClass.getAnnotation(Table.class);
				if(StringKit.isNotEmpty(db) && !db.equals(tableInfo.db()))
					continue;
				if(logger.isDebugEnabled())
					logger.debug("add table "+tableInfo.name());
				
				arp.addMapping(tableInfo.name(), tableInfo.pk(), tableClass);
			}
		}
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
