package cn.joy.plugin.qiniu;

import cn.joy.framework.plugin.PluginResourceBuilder;

public class QiniuResourceBuilder extends PluginResourceBuilder<QiniuResource> {

	@Override
	public QiniuResource build() {
		return new QiniuResource(this.name, this.prop);
	}

}
