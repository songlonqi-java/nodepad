package org.apache.dubbo.springboot.demo.consumer.Listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.dubbo.springboot.demo.consumer.Listener.MessageWrapper;
import java.io.Serializable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.opentracing.util.GlobalTracer;

public abstract class AbstractMessageListener<R extends Serializable, T> implements MessageListener<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageListener.class);
    @Override
    public void received(Consumer<T> consumer, Message<T> msg) {
        byte[] bytes = msg.getData();
        Map<String, String> properties = msg.getProperties();
        for (String key : properties.keySet()) {
            System.out.println("key:" + key + ", value:" + properties.get(key));
            /*
                key:x-datadog-parent-id, value:355377259536282153
                key:x-datadog-sampling-priority, value:1
                key:x-datadog-tags, value:_dd.p.dm=-1
                key:x-datadog-trace-id, value:4248638913769318426
             */
        }

      //  log.info("current traceiD:{}",GlobalTracer.get().activeSpan().context().toTraceId());

        String msgStr = new String(bytes);
        String messageId = msg.getMessageId().toString();
        log.info("consumer received topic:[{}] msg:[{}], msgId:[{}], msgKey:[{}]", msg.getTopicName(),
                msgStr, messageId, msg.getKey());

        try {
         //   MessageWrapper<R> msgWrapper = parseMsg(msgStr);
            long timestamp = System.currentTimeMillis();
          //  log.info("this topic:[{}], msgId:[{}], the msg born‘s uuid:[{}], timestamp:[{}].current timestamp:[{}]", msg.getTopicName(), messageId, msgWrapper.getUuid(), msgWrapper.getTimestamp(), timestamp);
            // 是否需要消费（消息幂等）
         //   boolean shouldConsume = shouldConsume(msgWrapper);
            if (true) {
                log.info("the msg which msgId is [{}] need consume", messageId);
                // consume message
                onConsumer((R) msgStr);
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

}
