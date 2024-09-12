package org.apache.dubbo.springboot.demo.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ConsumerApplication {
    private static final Logger log = LoggerFactory.getLogger(ConsumerApplication.class);

    public static void main(String[] args) throws MQClientException {
        // 创建消费者实例
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group");

        // 设置NameServer地址，多个地址用分号隔开
        consumer.setNamesrvAddr("localhost:9876");

        // 订阅Topic和Tag
        consumer.subscribe("test_topic", "*");

        // 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt message : messages) {
                    log.info("----------------------------------------------------：" + new String(message.getBody()));
                }

                // 消费成功
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 启动消费者
        consumer.start();

        System.out.println("消费者已启动...");
    }
}
