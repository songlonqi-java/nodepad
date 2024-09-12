package com.ec.test.mq.producer;

import org.springframework.stereotype.Component;

/**
 * @Description:
 * @author: lChen
 * @date: 2023-09-15 14:40
 **/
@Component
public class TestProducer extends AbstractProducer{
    @Override
    public String getTopicName() {
        return "persistent://public/default/TEST-TOPIC";
    }


}
