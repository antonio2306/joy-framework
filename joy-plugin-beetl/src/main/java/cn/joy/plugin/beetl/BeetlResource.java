package cn.joy.plugin.beetl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;

import cn.joy.framework.plugin.PluginResource;

public class BeetlResource extends PluginResource {
	GroupTemplate groupTemplate = null;
	
	public void merge(String tpl, Map<String, Object> datas, File outFile) {
		Template template = groupTemplate.getTemplate(tpl);
		template.binding(datas);

		try {
			template.renderTo(new FileOutputStream(outFile));
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public void release() {
		groupTemplate.close();
		groupTemplate = null;
	}
}
