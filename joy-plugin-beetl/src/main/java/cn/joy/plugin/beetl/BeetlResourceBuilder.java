package cn.joy.plugin.beetl;

import java.io.IOException;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;

import cn.joy.framework.plugin.PluginResourceBuilder;

public class BeetlResourceBuilder extends PluginResourceBuilder<BeetlResource>{
	private static ClasspathResourceLoader resourceLoader = null;
	private static Configuration cfg = null;

	static {
		try {
			resourceLoader = new ClasspathResourceLoader();
			cfg = Configuration.defaultConfiguration();
		} catch (IOException e) {
		}
	}
	
	@Override
	public BeetlResource build() {
		BeetlResource resource = new BeetlResource();
		resource.groupTemplate = new GroupTemplate(resourceLoader, cfg);
		return resource;
	}

}
