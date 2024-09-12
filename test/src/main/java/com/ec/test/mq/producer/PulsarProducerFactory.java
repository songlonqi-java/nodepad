package com.ec.test.mq.producer;


import com.ec.test.common.exception.ServiceException;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 消息发送生产者工厂
 * @author: lChen
 * @date: 2023-09-15 11:29
 **/
@Component
public class PulsarProducerFactory {


    private final Map<String, AbstractProducer> producerMap;

    public PulsarProducerFactory(@Autowired List<AbstractProducer> producers) {
        System.out.println("------------------"+producers);

        producerMap = producers.stream().collect(Collectors
                .toMap(AbstractProducer::getTopicName, Function.identity(), (v1, v2) -> v2));
    }

    /**
     * 一次性发送消息
     *
     * @param topicName topic名称
     * @param msgBody   消息体
     * @param msgKey    msg路由键
     */
    public void sendMsgOnce(String topicName, String msgBody, String msgKey) {
        AbstractProducer abstractProducer = producerMap.get(topicName);
        if (abstractProducer == null) {
         //   throw new ServiceException("通过topic名称:" + topicName + ",未找到对应的生产者。");
        }
        abstractProducer.sendMsg(msgBody, msgKey);
    }

}
