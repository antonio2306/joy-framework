package cn.joy.framework.kits;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;


public class BeetlKit {
	private static Logger logger = Logger.getLogger(BeetlKit.class);
	private static ClasspathResourceLoader resourceLoader;
	private static Configuration cfg;
	private static GroupTemplate gt;
	
	static{
		try {
			resourceLoader = new ClasspathResourceLoader();
			cfg = Configuration.defaultConfiguration();
			gt = new GroupTemplate(resourceLoader, cfg);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public static void merge(String tpl, Map<String, Object> datas, File outFile){
		Template t = gt.getTemplate(tpl);
		t.binding(datas);
		
		try {
			t.renderTo(new FileOutputStream(outFile));
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
