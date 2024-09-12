package com.ec.test.mq.producer;

import cn.hutool.json.JSONUtil;

import com.ec.test.common.bo.MessageWrapper;
import com.ec.test.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @Description: 生产者抽象类
 * @author: lChen
 * @date: 2023-05-04 11:14
 **/
@Slf4j
public abstract class AbstractProducer implements InitializingBean {

    @Autowired
    private PulsarClient pulsarClient;

    private Producer<byte[]> producer;


    /**
     * 生产者初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        String topicName = getTopicName();
        try {
            // 让pulsar自动生成生产者名称，这样可以多实例producer连接同一个topic
            producer = pulsarClient.newProducer()
                    .topic(topicName)
                    .compressionType(CompressionType.LZ4)
                    .create();
            log.info("[{}] was initialized with topic name:[{}].", this.getClass(), topicName);
        } catch (PulsarClientException e) {
            log.error("[{}] was initialized failed", this.getClass(), e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public abstract String getTopicName();

    /**
     * 发送消息
     */
    public <E extends Serializable> void sendMsg(E request, String key) {
        String msg = buildMsg(request);
        String topicName = getTopicName();
        try {
            MessageId messageId = doSend(msg, key);
            log.info(" delivered msg success messageId:[{}],data:[{}]", messageId, msg);
        } catch (Exception e) {
            log.error("topicName:[{}] delivered msg failed. msg:[{}], key:[{}]", topicName, msg, key, e);
            throw new ServiceException(e.getMessage(), e);
        }
    }


    protected MessageId doSend(String msg, String key) throws PulsarClientException {
        return producer.newMessage()
                .key(key)
                .value(msg.getBytes(StandardCharsets.UTF_8))
                .send();
    }


    /**
     * 生产者销毁
     */
    @PreDestroy
    protected void close() {
        try {
            producer.close();
            log.info("class:[{}] was destroying!", this.getClass());
        } catch (PulsarClientException e) {
            log.error("producer:[{}] was destroyed failed", this.getClass(), e);
        }
    }

    private <E extends Serializable> String buildMsg(E request) {
        MessageWrapper<E> messageWrapper = new MessageWrapper<>(request);
        return JSONUtil.toJsonStr(messageWrapper);
    }

}
