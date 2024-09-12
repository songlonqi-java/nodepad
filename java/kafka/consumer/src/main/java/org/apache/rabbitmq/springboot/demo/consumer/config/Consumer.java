package org.apache.rabbitmq.springboot.demo.consumer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class Consumer {
    // 指定要监听的 topic
    @KafkaListener(topics = "span-topic")
    public void consumeTopic(String msg) {
        // 参数: 从topic中收到的 value值
        System.out.println("收到的信息: " + msg);
    }
}
