package cn.joy.plugin.mqtt;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyCallback;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.plugin.ResourcePlugin;

@Plugin(key="mqtt")
public class MqttPlugin extends ResourcePlugin<MqttResourceBuilder, MqttResource>{
	public static MqttResourceBuilder builder() {
		return new MqttResourceBuilder();
	}

	public static MqttPlugin plugin() {
		return (MqttPlugin) JoyManager.plugin(MqttPlugin.class);
	}

	@Override
	public boolean start() {
		Prop prop = getConfig();
		String[] resources = prop.get("resources").split(",");

		for (String resource : resources) {
			Prop resProp = prop.getSubPropTrimPrefix(resource + ".");
			MqttResource pluginResource = builder().prop(resProp).name(resource).buildTo(this);
			if(this.mainResource==null)
				this.mainResource = pluginResource;
		}

		logger.info("mqtt plugin start success");
		return true;
	}

	@Override
	public void stop() {
		
	}
	
	public static MqttResource use() {
		return plugin().useResource();
	}

	public static MqttResource use(String name) {
		return plugin().useResource(name);
	}
	
	public static void unuse(String name) {
		plugin().unuseResource(name);
	}
	
	public static void publish(String topic, byte[] content, int qos) {
		use().publish(topic, content, qos);
	}
	
	public static void publish(String topic, byte[] content, int qos, boolean retained, Object userContext, final MqttActionCallback callback) {
		use().publish(topic, content, qos, retained, userContext, callback);
	}

	public static void subscribe(String topic, int qos) {
		use().subscribe(topic, qos);
	}
	
}
