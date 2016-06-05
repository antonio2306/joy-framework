package cn.joy.plugin.rocketmq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;

public interface MessageHandler{

    ConsumeConcurrentlyStatus handle(MessageExt message, ConsumeConcurrentlyContext context);
}
