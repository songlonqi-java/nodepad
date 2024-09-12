package com.ec.test.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ec.test.common.bo.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;

import java.io.Serializable;

/**
 * @Description: 消费者抽象
 * @author: lChen
 * @date: 2023-04-27 22:14
 **/
@Slf4j
public abstract class AbstractMessageListener<R extends Serializable, T> implements MessageListener<T> {


    @Override
    public void received(Consumer<T> consumer, Message<T> msg) {
        byte[] bytes = msg.getData();
        String msgStr = new String(bytes);
        String messageId = msg.getMessageId().toString();
        log.info("consumer received topic:[{}] msg:[{}], msgId:[{}], msgKey:[{}]", msg.getTopicName(),
                msgStr, messageId, msg.getKey());

        try {
            MessageWrapper<R> msgWrapper = parseMsg(msgStr);
            long timestamp = System.currentTimeMillis();
            log.info("this topic:[{}], msgId:[{}], the msg born‘s uuid:[{}], timestamp:[{}].current timestamp:[{}]",
                    msg.getTopicName(), messageId, msgWrapper.getUuid(), msgWrapper.getTimestamp(), timestamp);
            // 是否需要消费（消息幂等）
            boolean shouldConsume = shouldConsume(msgWrapper);
            if (shouldConsume) {
                log.info("the msg which msgId is [{}] need consume", messageId);
                // consume message
                onConsumer(msgWrapper.getMessage());
            }
            consumer.acknowledge(msg);
            log.info("the msg which id is [{}] has been consumed successful", messageId);
        } catch (Exception e) {
            log.error(" the mq which id is [{}] has been consumed failed. plz check the MQ server or MQ client",
                    messageId, e);
            // 告知broker并未成功消费该条消息，broker收到该请求后，会触发broker将这条消息重新下发给消费者进行消费。
            // 如果消费者订阅模式为Exclusive或者Failover subscription类型时，消费者只能否认收到的最后一条消息。
            // 如果消费者订阅模式为Shared或者Key_Shared类型时，消费者可以否认单独一条消息。
            consumer.negativeAcknowledge(msg);
        }
    }

    /**
     * 消费msg逻辑
     *
     * @param msg 消息
     */
    protected abstract void onConsumer(R msg);


    /**
     * 序列化消息
     *
     * @param msgStr 消息
     * @return 序列化结果
     */
    protected MessageWrapper<R> parseMsg(String msgStr) {
        return JSON.parseObject(msgStr, new TypeReference<MessageWrapper<R>>() {
        });
    }

    /**
     * 是否需要消费,用于处理幂等
     *
     * @param msg 消息
     * @return 是否需要消费
     */
    protected boolean shouldConsume(MessageWrapper<R> msg) {
        return true;
    }


}
