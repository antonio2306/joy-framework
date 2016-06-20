package cn.joy.plugin.mqtt;

public interface MqttActionCallback {
	void run(String response, Object userContext, Throwable exception, Object token);
}
