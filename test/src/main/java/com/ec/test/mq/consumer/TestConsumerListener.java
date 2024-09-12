package com.ec.test.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @author: lChen
 * @date: 2023-09-15 16:09
 **/
@Component
@Slf4j
public class TestConsumerListener extends AbstractMessageListener<String, byte[]> {
    @Override
    protected void onConsumer(String msg) {
        log.info("msg:[{}] has consumed", msg);
    }


}
