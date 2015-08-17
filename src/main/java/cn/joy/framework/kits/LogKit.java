package cn.joy.framework.kits;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LogKit{
	public static Logger getDailyLogger(String name) {  
        Logger logger = Logger.getLogger(name);  
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
	
	public static void main(String[] args){
		getDailyLogger("111");
		getDailyLogger("111");
		getDailyLogger("1112");
	}
}
