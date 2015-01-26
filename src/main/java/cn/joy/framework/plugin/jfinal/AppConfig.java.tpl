package cn.joy.framework.plugin.jfinal;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;

public class AppConfig extends BaseConfig{
	private static Logger logger = Logger.getLogger(DemoConfig.class);

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
		me.setViewType(ViewType.JSP); 
		loadPropertyFile("jdbc.properties");	
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/", XxxController.class);
		me.add("/service/open", OpenRuleController.class);
		me.add("/service/business", BusinessRuleController.class);
		me.add("/service/download", DownloadRuleController.class);
		me.add("/webproxy", WebProxyController.class);
	}

	@Override
	public void configPlugin(Plugins me) {
		String[] dbs = getProperty("dbs").split(",");
		ActiveRecordPlugin [] arps = new ActiveRecordPlugin [dbs.length];
		for(int i=0;i<dbs.length;i++){
			// 配置C3p0数据库连接池插件
			String jdbcUrl="jdbc:mysql://" + getProperty(dbs[i]+".dbip") +":"+getProperty(dbs[i]+".dbport")+ "/" + getProperty(dbs[i]+".dbname") ;//+ "?characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull";
			
			C3p0Plugin c3p0Plugin = new C3p0Plugin(jdbcUrl, getProperty(dbs[i]+".dbuser"), getProperty(dbs[i]+".dbpassword").trim());
			me.add(c3p0Plugin);

			// 配置ActiveRecord插件
			arps[i] = new ActiveRecordPlugin(dbs[i],c3p0Plugin);
			me.add(arps[i]);
			//arps[i].setCache(new EhCache());
			
		}
		//arps[0].addMapping("xxxx", Xxxx.class);	
	}

	@Override
	public void configInterceptor(Interceptors me) {
		
	}

	@Override
	public void configHandler(Handlers me) {
		
	}

	@Override
	public void afterJFinalStart() {
		super.afterJFinalStart();
		//用户需要自定义安全管理器，检查请求的安全性
		JoyManager.setRouteStore(new DefaultRouteStore());
		//用户需要自定义服务器路由存储
		JoyManager.setSecurityManager(new DefaultSecurityManager());
		
		try{
			JoyManager.init();
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}
