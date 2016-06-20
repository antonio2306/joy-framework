package cn.joy.plugin.test.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.annotations.Test;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.PropKit;
import cn.joy.framework.test.TestExecutor;
import cn.joy.plugin.mqtt.Mqtt;
import cn.joy.plugin.mqtt.MqttActionCallback;

@Test(groups = "case.mqtt.subscribe", dependsOnGroups = "case.init")
public class TestSubscribe {
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executePluginGroup("case.mqtt.subscribe");
	}

	public void testSubscribe() {
		if (!Mqtt.plugin().isStarted())
			return;
		/*Mqtt.builder().callback(new MqttCallback() {
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				System.out.println("topic:"+topic);
				System.out.println("message:"+message);
			}
			
			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				
			}
			
			@Override
			public void connectionLost(Throwable cause) {
				
			}
		}).name("testSubscribe").prop(PropKit.empty().set("server_url", Mqtt.use().getConfig().get("server_url"))
				.set("client_id", "test_Subscribe").set("user", Mqtt.use().getConfig().get("user"))
				.set("password", Mqtt.use().getConfig().get("password")))
				.buildTo(Mqtt.plugin());
		System.out.println(Mqtt.use("testSubscribe").getConfig().get("client_id"));
		Mqtt.use("testSubscribe").subscribe("push/test", 1);*/
		Mqtt.use().setCallback(new MqttCallback() {
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				System.out.println("topic:"+topic);
				System.out.println("message:"+message);
			}
			
			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				
			}
			
			@Override
			public void connectionLost(Throwable cause) {
				
			}
		}).subscribe("push/tests", 1);
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
		}

		JoyManager.destroy();
	}

}