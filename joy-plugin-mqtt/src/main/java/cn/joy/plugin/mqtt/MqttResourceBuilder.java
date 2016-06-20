package cn.joy.plugin.mqtt;

import org.eclipse.paho.client.mqttv3.MqttCallback;

import cn.joy.framework.plugin.PluginResourceBuilder;

public class MqttResourceBuilder extends PluginResourceBuilder<MqttResource> {
	private MqttCallback callback;
	
	public MqttResourceBuilder callback(MqttCallback callback){
		this.callback = callback;
		return this;
	}

	@Override
	public MqttResource build() {
		return new MqttResource(name, prop, callback);
	}

}
