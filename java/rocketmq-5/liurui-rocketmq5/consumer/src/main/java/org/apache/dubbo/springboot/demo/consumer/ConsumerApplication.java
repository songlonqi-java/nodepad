package org.apache.dubbo.springboot.demo.consumer;

import java.io.IOException;
import java.util.Collections;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsumerApplication {
    private static final Logger log = LoggerFactory.getLogger(ConsumerApplication.class);

    public static void main(String[] args) throws InterruptedException, ClientException, IOException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        // Credential provider is optional for client configuration.
        String accessKey = "Vbbh8ICl0d6YY8eB";
        String secretKey = "8Jfjh1WmupRQ3aUD";
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(accessKey, secretKey);

        String endpoints = "rmq-cn-5yd3ar5b30j.cn-hangzhou.rmq.aliyuncs.com:8080";
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
                // client configuration to solve the problem please if SSL is not essential.
                // .enableSsl(false)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();
        String tag = "yourMessageTagA";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
        String consumerGroup = "yourConsumerGroup";
        String topic = "Topic-0";
        // In most case, you don't need to create too many consumers, singleton pattern is recommended.
        PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the consumer group name.
                .setConsumerGroup(consumerGroup)
                // Set the subscription for the consumer.
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                .setMessageListener(messageView -> {
                    // Handle the received message and return consume result.
                    log.info("Consume message={}", messageView);
                    return ConsumeResult.SUCCESS;
                })
                .build();
        // Block the main thread, no need for production environment.
        Thread.sleep(Long.MAX_VALUE);
        // Close the push consumer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
        pushConsumer.close();
    }
}
