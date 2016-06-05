package cn.joy.plugin.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.MQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

import cn.joy.framework.annotation.Plugin;
import cn.joy.framework.core.JoyManager;
import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.plugin.ResourcePlugin;

@Plugin(key="rocketmq")
public class RocketmqPlugin extends ResourcePlugin<RocketmqResourceBuilder, RocketmqResource>{
	public static RocketmqResourceBuilder builder() {
		return new RocketmqResourceBuilder();
	}

	public static RocketmqPlugin plugin() {
		return (RocketmqPlugin) JoyManager.plugin("rocketmq");
	}

	@Override
	public boolean start() {
		Prop prop = getConfig();
		String[] resources = prop.get("resources").split(",");

		for (String resource : resources) {
			Prop resProp = prop.getSubPropTrimPrefix(resource + ".");
			RocketmqResource pluginResource = builder().prop(resProp).name(resource).buildTo(this);
			if(this.mainResource==null)
				this.mainResource = pluginResource;
		}

		logger.info("rocketmq plugin start success");
		return true;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	public static RocketmqResource use() {
		return plugin().useResource();
	}

	public static RocketmqResource use(String name) {
		return plugin().useResource(name);
	}
	
	public static void unuse(String name) {
		plugin().unuseResource(name);
	}
	
	public static MQProducer useProducer() {
		return use().useProducer();
	}
	
	public static MQProducer useProducer(String producerGroup) {
		return use().useProducer(producerGroup);
	}
	
	public static MQPushConsumer usePushConsumer(String consumerGroup, String topic, String tag, MessageListener messageListener) {
		return use().usePushConsumer(consumerGroup, topic, tag, messageListener);
	}

	public static MQPushConsumer usePushConsumer(String consumerGroup, String topic, String tag, ConsumeFromWhere fromWhere,
			int batchMaxSize, MessageModel model) {
		return use().usePushConsumer(consumerGroup, topic, tag, fromWhere, batchMaxSize, model);
	}

	public static SendResult send(Message message) {
		return use().send(message);
	}

	public static SendResult sendTimeout(Message message, int timeout) {
		return use().sendTimeout(message, timeout);
	}

	public static void sendOneway(Message message) {
		use().sendOneway(message);
	}
	
	public static SendResult send(String producerGroup, Message message) {
		return use().send(producerGroup, message);
	}

	public static SendResult sendTimeout(String producerGroup, Message message, int timeout) {
		return use().sendTimeout(producerGroup, message, timeout);
	}

	public static void sendOneway(String producerGroup, Message message) {
		use().sendOneway(producerGroup, message);
	}
	
}
