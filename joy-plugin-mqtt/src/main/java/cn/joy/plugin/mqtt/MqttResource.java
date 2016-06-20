package cn.joy.plugin.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.PluginResource;

public class MqttResource extends PluginResource {
	private MqttClient client;
	private MqttConnectOptions connOpts;
	private MqttClientPersistence persistence = new MemoryPersistence();
	
	MqttResource(String name, Prop prop) {
		this(name, prop, null);
	}

	MqttResource(String name, Prop prop, MqttCallback callback) {
		this.prop = prop;

		try {
			client = new MqttClient(prop.get("server_url"),
					StringKit.getString(prop.get("client_id"), MqttClient.generateClientId()), persistence);

			connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(prop.getBoolean("clean_session", true));
			connOpts.setKeepAliveInterval(prop.getInt("keep_alive_interval", 60));
			connOpts.setConnectionTimeout(prop.getInt("connection_timeout", 30));
			String userName = prop.get("user");
			String password = prop.get("password");
			if (StringKit.isNotEmpty(userName) && StringKit.isNotEmpty(password)) {
				connOpts.setUserName(userName);
				connOpts.setPassword(password.toCharArray());
			}
			
			if(callback!=null)
				client.setCallback(callback);

			logger.debug("Client {} connecting to server {} ", client.getClientId(), client.getServerURI());
			client.connect(connOpts);
			logger.debug("Connected");
		} catch (Exception e) {
			throw new RuntimeException("build mqtt resource error", e);
		}
	}

	public MqttResource reconnect() {
		if (client != null && !client.isConnected()) {
			try {
				logger.debug("Client {} reconnecting to server {} ", client.getClientId(), client.getServerURI());
				client.connect(connOpts);
				logger.debug("Reconnected");
			} catch (Exception e) {
				throw new RuntimeException("build mqtt resource error", e);
			}
		}
		return this;
	}

	@Override
	public void release() {
		try {
			if (client != null && client.isConnected())
				client.disconnect();
		} catch (MqttException e) {
			logger.error("", e);
		}
	}
	
	public MqttClient getClient(){
		return this.client;
	}
	
	public MqttResource setCallback(MqttCallback callback){
		client.setCallback(callback);
		return this;
	}

	public void publish(String topic, byte[] content, int qos) {
		publish(topic, content, qos, false, null, null);
	}
	
	public void publish(String topic, byte[] content, int qos, boolean retained, Object userContext, final MqttActionCallback callback) {
		MqttMessage message = new MqttMessage(content);
        message.setQos(qos);
        message.setRetained(false);
        
        /*client.setCallback(new MqttCallback() {
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				
			}
			
			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				try {
					if(callback!=null)
						callback.run(token.getResponse(), token);
				} catch (Exception e) {
					logger.error("", e);
				}
			}
			
			@Override
			public void connectionLost(Throwable cause) {
				try {
					if(connectionLostCallback!=null)
						connectionLostCallback.run("", cause);
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		});*/
        try {
			client.publish(topic, message, userContext, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					try {
						if(callback!=null)
							callback.run(asyncActionToken.getResponse().toString(), asyncActionToken.getUserContext(), null, asyncActionToken);
					} catch (Exception e) {
						logger.error("", e);
					}
				}
				
				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					try {
						if(callback!=null)
							callback.run(asyncActionToken.getResponse().toString(), asyncActionToken.getUserContext(), exception, asyncActionToken);
					} catch (Exception e) {
						logger.error("", e);
					}
					
				}
			});
		} catch (Exception e) {
			logger.error("", e);
			try {
				if(callback!=null)
					callback.run("error", userContext, e, null);
			} catch (Exception e1) {
				logger.error("", e1);
			}
		}
	}
	
	public void subscribe(String topic, int qos) {
		try {
			client.subscribe(topic, qos);
		} catch (MqttException e) {
			throw new RuntimeException("subscribe fail", e);
		}
	}
}
