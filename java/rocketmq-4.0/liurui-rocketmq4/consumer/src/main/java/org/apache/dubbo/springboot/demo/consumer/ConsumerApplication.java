package org.apache.dubbo.springboot.demo.consumer;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;


import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import org.apache.rocketmq.client.apis.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsumerApplication {
    private static final Logger log = LoggerFactory.getLogger(ConsumerApplication.class);

    public static void main(String[] args) throws ClientException, IOException, InterruptedException {
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(PropertyKeyConst.GROUP_ID, "group01");
        consumerProperties.setProperty(PropertyKeyConst.AccessKey, "90O8841hO048v50t");
        consumerProperties.setProperty(PropertyKeyConst.SecretKey, "o079cndkTw0wESGB");
        consumerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, "rmq-cn-uax31r38q14.cn-hangzhou.rmq.aliyuncs.com:8080");
        //注意！！！如果访问阿里云RocketMQ 5.0系列实例，不要设置PropertyKeyConst.INSTANCE_ID，否则会导致收发失败
        Consumer consumer = ONSFactory.createConsumer(consumerProperties);
        consumer.subscribe("topic01", "yourMessageTagA", new MessageListener() {
            @Override
            public Action consume(Message message, ConsumeContext consumeContext) {
                System.out.println(new Date() + " Receive message, Topic is:" + message.getTopic() + ", MsgId is:" + message.getMsgID());
                //如果想测试消息重投的功能,可以将Action.CommitMessage 替换成Action.ReconsumeLater；
                return Action.CommitMessage;
            }
        });
        consumer.start();
        System.out.println("Consumer start success.");

        //等待固定时间防止进程退出
        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
