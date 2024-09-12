package org.apache.rabbitmq.springboot.demo.provider;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.rocketmq.client.apis.ClientException;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoServiceImpl  {
    private static final Logger log = LoggerFactory.getLogger(DemoServiceImpl.class);
    public static void main(String[] args)  throws ClientException, IOException{
        Properties producerProperties = new Properties();
        producerProperties.setProperty(PropertyKeyConst.AccessKey, "90O8841hO048v50t");
        producerProperties.setProperty(PropertyKeyConst.SecretKey, "o079cndkTw0wESGB");
        producerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, "rmq-cn-uax31r38q14.cn-hangzhou.rmq.aliyuncs.com:8080");
        //注意！！！如果访问阿里云RocketMQ 5.0系列实例，不要设置PropertyKeyConst.INSTANCE_ID，否则会导致收发失败
        Producer producer = ONSFactory.createProducer(producerProperties);
        producer.start();
        System.out.println("Producer Started");

        for (int i = 0; i < 10; i++) {
            Message message = new Message("topic01", "yourMessageTagA", "mq send transaction message test".getBytes());
            try {
                SendResult sendResult = producer.send(message);
                assert sendResult != null;
                System.out.println(new Date() + " Send mq message success! Topic is:" + "topic01" + " msgId is: " + sendResult.getMessageId());
            } catch (ONSClientException e) {
                // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
                System.out.println(new Date() + " Send mq message failed! Topic is:" + "topic01");
                e.printStackTrace();
            }
        }

    }

}
