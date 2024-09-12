package com.ec.test.mq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 消费者配置
 * @author: lChen
 * @date: 2023-05-10 17:06
 **/
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ConsumerConfig {

    private final PulsarClient pulsarClient;

    @Value("${spring.application.name:test-consumer}")
    private String appName;

    private final TestConsumerListener testConsumerListener;

   // private final UserConsumerListener userListener;

    @Bean
    public Consumer<byte[]> createConsumer() {
        try {
            Consumer<byte[]> consumer = pulsarClient.newConsumer()
                    .topic("persistent://public/default/TEST-TOPIC")
                    .subscriptionName(appName)
                    .subscriptionType(SubscriptionType.Shared)
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    // 启动阶梯式重试，类似RocketMQ的阶梯重试，注意：否定ack，消息重试由客户端发起，而retryTopic是由broker发起
                    .messageListener(testConsumerListener)
                    .subscribe();
            log.info("init pulsar consumer success");
            return consumer;
        } catch (PulsarClientException e) {
            log.error("topicName:[{}]初始化 pulsar consumer 失败", "persistent://public/default/TEST-TOPIC", e);
            throw new IllegalArgumentException("初始化pulsar consumer 失败");
        }
    }





}
