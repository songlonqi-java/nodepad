package org.apache.dubbo.springboot.demo.provider;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class DemoServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(DemoServiceImpl.class);

    private static final String ACCESS_KEY = "Vbbh8ICl0d6YY8eB";
    private static final String SECRET_KEY = "8Jfjh1WmupRQ3aUD";
    private static final String ENDPOINTS = "rmq-cn-5yd3ar5b30j.cn-hangzhou.rmq.aliyuncs.com:8080";

    public static void main(String[] args) throws Exception {
        // 创建生产者实例
        DefaultMQProducer producer = new DefaultMQProducer("producer_group");

        // 设置NameServer地址，多个地址用分号隔开
        producer.setNamesrvAddr("localhost:9876");

        // 启动生产者
        producer.start();

        try {
            // 创建消息实例，指定Topic、Tag和消息内容
            Message message = new Message("test_topic", "tag", "Hello, RocketMQ!".getBytes());

            // 发送消息并获取发送结果
            SendResult sendResult = producer.send(message);

            log.info("消息发送成功，消息ID：" + sendResult.getMsgId());
            log.info("--------------------------------------------" );
        } finally {
            // 关闭生产者
            producer.shutdown();
        }
    }
}
