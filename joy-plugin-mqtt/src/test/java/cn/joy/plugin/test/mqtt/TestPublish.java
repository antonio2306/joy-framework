package cn.joy.plugin.test.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.testng.annotations.Test;

import cn.joy.framework.core.JoyManager;
import cn.joy.framework.test.TestExecutor;
import cn.joy.plugin.mqtt.Mqtt;
import cn.joy.plugin.mqtt.MqttActionCallback;

@Test(groups = "case.mqtt", dependsOnGroups = "case.init")
public class TestPublish {
	@Test(enabled = false)
	public static void main(String[] args) {
		TestExecutor.executePluginGroup("case.mqtt");
	}

	public void testPublish() {
		if (!Mqtt.plugin().isStarted())
			return;
		Mqtt.publish("push/tests", "hello".getBytes(), 1, false, "msg1", new MqttActionCallback() {
			@Override
			public void run(String response, Object userContext, Throwable exception, Object token) {
				System.out.println(response);
				System.out.println(userContext);
				System.out.println(exception);
			}
			
		});
		
		/*Mqtt.use().setCallback(new MqttCallback() {
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
		}).subscribe("push/test2", 1);*/

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		JoyManager.destroy();
	}

}