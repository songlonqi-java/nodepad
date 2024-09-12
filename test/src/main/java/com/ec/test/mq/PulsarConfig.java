package com.ec.test.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(PulsarProperties.class)
@Slf4j
@RequiredArgsConstructor
public class PulsarConfig {

    private final PulsarProperties pulsarProperties;


    @Bean("pulsarClient")
    public PulsarClient getPulsarClient() {
        try {
            PulsarClient client = PulsarClient.builder()
                    //.authentication(AuthenticationFactory.token(pulsarProperties.getToken()))
                    .serviceUrl(pulsarProperties.getServiceUrl())
                    .build();
            log.info("init pulsar client success");
            return client;
        } catch (PulsarClientException e) {
            log.error("pulsar初始化失败", e);
            throw new RuntimeException("init pulsar client failed");
        }
    }

}
