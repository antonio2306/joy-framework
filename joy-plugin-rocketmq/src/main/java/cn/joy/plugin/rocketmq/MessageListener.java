package cn.joy.plugin.rocketmq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;

import cn.joy.framework.kits.CollectionKit;
import cn.joy.framework.kits.LogKit;
import cn.joy.framework.kits.LogKit.Log;
import cn.joy.framework.kits.StringKit;

public class MessageListener implements MessageListenerConcurrently {
	protected Log logger = LogKit.getLog(MessageListener.class);
    private Map<String, Map<String, MessageHandler>> handlers = new HashMap<>();
    
    MessageListener(){}
    
    public static MessageListener create(){
    	return new MessageListener();
    }
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
    	//logger.debug("consume msgs:{}", msgs);

        for (MessageExt message : msgs) {
            String topic = message.getTopic();

            Map<String, MessageHandler> topicHandlers = handlers.get(topic);
            if (CollectionKit.isEmpty(topicHandlers))
                throw new RuntimeException("no handler for topic "+topic);

            MessageHandler messageHandler = null;
            String tag = StringKit.getString(message.getTags(), "*");
            messageHandler = topicHandlers.get(tag);
            if (messageHandler == null) 
                throw new RuntimeException("no handler for topic "+topic + " tag=" + tag);
            
            return messageHandler.handle(message, context);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public MessageListener addTopicHandler(String topic, String tag, MessageHandler topicHandler) {
    	Map<String, MessageHandler> topicHandlers = handlers.get(topic);
    	if(topicHandlers==null){
    		topicHandlers = new HashMap<>();
    		handlers.put(topic, topicHandlers);
    	}
    	topicHandlers.put(tag, topicHandler);
    	return this;
    }
}
