package cn.joy.framework.kits;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogKit{
	private static Log log = null;
	private static final Map<String, Log> map = new HashMap<String, Log>();
	
	private LogKit() {}
	
	public static Log use() {
		return use("joy");
	}
	
	public static Log use(String logKey) {
		Log result = map.get(logKey);
		if (result == null) {
			synchronized (map) {
				result = map.get(logKey);
				if (result == null) {
					result = new Log(logKey);
					map.put(logKey, result);
					if (LogKit.log == null)
						LogKit.log = result;
				}
			}
		}
		return result;
	}
	
	public static class Log {
		private Logger logger;
		
		Log(String logKey){
			this.logger = LoggerFactory.getLogger(logKey);
		}
		
		public boolean isDebugEnabled(){
			return logger.isDebugEnabled();
		}
		
		public boolean isInfoEnabled(){
			return logger.isInfoEnabled();
		}
		
		public boolean isWarnEnabled(){
			return logger.isWarnEnabled();
		}
		
		public boolean isErrorEnabled(){
			return logger.isErrorEnabled();
		}
		
		public void debug(String msg){
			if(logger.isDebugEnabled())
				logger.debug(msg);
		}
		
		public void debug(String format, Object... params){
			if(logger.isDebugEnabled())
				logger.debug(format, params);
		}
		
		public void info(String msg){
			if(logger.isInfoEnabled())
				logger.info(msg);
		}
		
		public void info(String format, Object... params){
			if(logger.isInfoEnabled())
				logger.info(format, params);
		}
		
		public void warn(String msg){
			if(logger.isWarnEnabled())
				logger.warn(msg);
		}
		
		public void warn(String format, Object... params){
			if(logger.isWarnEnabled())
				logger.warn(format, params);
		}
		
		public void error(String msg){
			if(logger.isErrorEnabled())
				logger.error(msg);
		}
		
		public void error(String format, Object... params){
			if(logger.isErrorEnabled())
				logger.error(format, params);
		}
		
		public void error(String msg, Throwable t){
			if(logger.isErrorEnabled())
				logger.error(msg, t);;
		}
	}
	
	public static boolean isDebugEnabled(){
		return use().isDebugEnabled();
	}
	
	public static boolean isInfoEnabled(){
		return use().isInfoEnabled();
	}
	
	public static boolean isWarnEnabled(){
		return use().isWarnEnabled();
	}
	
	public static boolean isErrorEnabled(){
		return use().isErrorEnabled();
	}
	
	public static void debug(String msg){
		if(use().isDebugEnabled())
			use().debug(msg);
	}
	
	public static void debug(String format, Object... params){
		if(use().isDebugEnabled())
			use().debug(format, params);
	}
	
	public static void info(String msg){
		if(use().isInfoEnabled())
			use().info(msg);
	}
	
	public static void info(String format, Object... params){
		if(use().isInfoEnabled())
			use().info(format, params);
	}
	
	public static void warn(String msg){
		if(use().isWarnEnabled())
			use().warn(msg);
	}
	
	public static void warn(String format, Object... params){
		if(use().isWarnEnabled())
			use().warn(format, params);
	}
	
	public static void error(String msg){
		if(use().isErrorEnabled())
			use().error(msg);
	}
	
	public static void error(String format, Object... params){
		if(use().isErrorEnabled())
			use().error(format, params);
	}
	
	public static void error(String msg, Throwable t){
		if(use().isErrorEnabled())
			use().error(msg, t);;
	}
	
	public static void error(Throwable t){
		if(use().isErrorEnabled())
			use().error("", t);;
	}
	
	public static org.apache.log4j.Logger getDailyLogger(String name) {  
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(name);  
        if(!logger.getAllAppenders().hasMoreElements()){
            logger.setLevel(Level.DEBUG);  
            // 是否继承父Logger
            logger.setAdditivity(true);  
            // 生成新的Appender  
            DailyRollingFileAppender appender = new DailyRollingFileAppender();  
            PatternLayout layout = new PatternLayout();  
            // log的输出形式  
            String conversionPattern = "%d %-5p %F %L - %m%n"; 
            layout.setConversionPattern(conversionPattern);  
            appender.setLayout(layout);  
            
            appender.setDatePattern("'.'yyyy-MM-dd'.log'");
            // log输出路径  
            String dirPath = PathKit.getWebRootPath()+"/logs/"+name;
            //File dir = new File(dirPath);
            //if(!dir.exists())
            //	dir.mkdirs();
            
            appender.setFile(dirPath + File.separator + name + ".log");  
            appender.setEncoding("UTF-8");  
            // true:在已存在log文件后面追加 false:新log覆盖以前的log  
            appender.setAppend(true);  
            // 适用当前配置  
            appender.activateOptions();  
            // 将新的Appender加到Logger中  
            logger.addAppender(appender);  
        }
        
        return logger;  
    }  
	

}
