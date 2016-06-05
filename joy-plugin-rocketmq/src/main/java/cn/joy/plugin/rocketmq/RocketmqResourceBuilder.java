package cn.joy.plugin.rocketmq;

import cn.joy.framework.plugin.PluginResourceBuilder;

public class RocketmqResourceBuilder extends PluginResourceBuilder<RocketmqResource> {

	@Override
	public RocketmqResource build() {
		return new RocketmqResource(name, prop);
	}

}
