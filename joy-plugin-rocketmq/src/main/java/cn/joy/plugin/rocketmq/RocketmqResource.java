package cn.joy.plugin.rocketmq;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.MQPullConsumer;
import com.alibaba.rocketmq.client.consumer.MQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

import cn.joy.framework.kits.PropKit.Prop;
import cn.joy.framework.kits.StringKit;
import cn.joy.framework.plugin.PluginResource;

public class RocketmqResource extends PluginResource {
	private String nameServer;
	private MQProducer defaultProducer;
	private Map<String, MQProducer> producers = new HashMap<>();
	private Map<String, MQPushConsumer> pushConsumers = new HashMap<>();
	private Map<String, MQPullConsumer> pullConsumers = new HashMap<>();

	RocketmqResource(String name, Prop prop) {
		this.nameServer = prop.get("name_server");

		String defaultProducerGroup = prop.get("default_producer_group");
		if (StringKit.isNotEmpty(defaultProducerGroup)) {
			defaultProducer = useProducer(defaultProducerGroup);
		}
	}

	@Override
	public void release() {
		for (Entry<String, MQProducer> entry : producers.entrySet()) {
			entry.getValue().shutdown();
		}
		producers.clear();

		for (Entry<String, MQPushConsumer> entry : pushConsumers.entrySet()) {
			entry.getValue().shutdown();
		}
		pushConsumers.clear();

		for (Entry<String, MQPullConsumer> entry : pullConsumers.entrySet()) {
			entry.getValue().shutdown();
		}
		pullConsumers.clear();
	}
	
	public MQProducer useProducer() {
		return defaultProducer;
	}

	public MQProducer useProducer(String producerGroup) {
		if(StringKit.isEmpty(producerGroup))
			return defaultProducer;
		MQProducer producer = producers.get(producerGroup);
		if(producer!=null)
			return producer;
		
		try {
			DefaultMQProducer mqProducer = new DefaultMQProducer(producerGroup);
			mqProducer.setNamesrvAddr(nameServer);
			mqProducer.start();
			producers.put(producerGroup, mqProducer);
			return mqProducer;
		} catch (MQClientException e) {
			throw new RuntimeException("start producer error", e);
		}
	}

	public MQPushConsumer usePushConsumer(String consumerGroup, String topic, String tag, MessageListener messageListener) {
		return usePushConsumer(consumerGroup, topic, tag, ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET, 15,
				MessageModel.CLUSTERING);
	}

	public MQPushConsumer usePushConsumer(String consumerGroup, String topic, String tag, ConsumeFromWhere fromWhere,
			int batchMaxSize, MessageModel model) {
		try {
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
			consumer.setNamesrvAddr(nameServer);
			consumer.subscribe(topic, tag);
			consumer.setConsumeFromWhere(fromWhere);
			consumer.setConsumeMessageBatchMaxSize(batchMaxSize);
			consumer.setMessageModel(model);
			// consumer.setConsumeTimestamp(UtilAll.timeMillisToHumanString3(System.currentTimeMillis()
			// - (1000 * 60 * minutesBefore))); //回溯时间
			// consumer.setPullThresholdForQueue(1000); //本地缓存上限
			// consumer.setPullInterval(0); //拉取间隔，默认长轮询
			// consumer.setPullBatchSize(32); //每次拉取数

			consumer.start();
			pushConsumers.put(consumerGroup, consumer);
			return consumer;
		} catch (MQClientException e) {
			throw new RuntimeException("start push consumer error", e);
		}
	}

	public SendResult send(Message message) {
		return send(useProducer(), message);
	}

	public SendResult sendTimeout(Message message, int timeout) {
		return sendTimeout(useProducer(), message, timeout);
	}

	public void sendOneway(Message message) {
		sendOneway(useProducer(), message);
	}
	
	public SendResult send(String producerGroup, Message message) {
		return send(useProducer(producerGroup), message);
	}

	public SendResult sendTimeout(String producerGroup, Message message, int timeout) {
		return sendTimeout(useProducer(producerGroup), message, timeout);
	}

	public void sendOneway(String producerGroup, Message message) {
		sendOneway(useProducer(producerGroup), message);
	}
	
	private SendResult send(MQProducer producer, Message message) {
		try {
			SendResult result = producer.send(message);
			logger.debug("topic={}, tag={}, key={}, msgId={}, sendStatus={}", message.getTopic(), message.getTags(),
					message.getKeys(), result.getMsgId(), result.getSendStatus());
			return result;
		} catch (Exception e) {
			throw new RuntimeException("sendMessage error", e);
		}
	}

	private SendResult sendTimeout(MQProducer producer, Message message, int timeout) {
		try {
			SendResult result = producer.send(message, timeout);
			logger.debug("topic={}, tag={}, key={}, msgId={}, sendStatus={}", message.getTopic(), message.getTags(),
					message.getKeys(), result.getMsgId(), result.getSendStatus());
			return result;
		} catch (Exception e) {
			throw new RuntimeException("sendTimeoutMessage error", e);
		}
	}

	private void sendOneway(MQProducer producer, Message message) {
		try {
			producer.sendOneway(message);
		} catch (Exception e) {
			throw new RuntimeException("sendMessageOneway error", e);
		}
	}
}
