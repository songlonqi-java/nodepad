package org.apache.dubbo.springboot.demo.provider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DemoServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(DemoServiceImpl.class);

    private static final String ACCESS_KEY = "Vbbh8ICl0d6YY8eB";
    private static final String SECRET_KEY = "8Jfjh1WmupRQ3aUD";
    private static final String ENDPOINTS = "rmq-cn-5yd3ar5b30j.cn-hangzhou.rmq.aliyuncs.com:8080";

    public static void main(String[] args) throws ClientException, IOException {
        String topic = "Topic-0";
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        // Credential provider is optional for client configuration.
        // This parameter is necessary only when the server ACL is enabled. Otherwise,
        // it does not need to be set by default.
        SessionCredentialsProvider sessionCredentialsProvider =
                new StaticSessionCredentialsProvider(ACCESS_KEY, SECRET_KEY);
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(ENDPOINTS)
                // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
                // client configuration to solve the problem please if SSL is not essential.
                // .enableSsl(false)
                .setCredentialProvider(sessionCredentialsProvider)
                .build();
        final ProducerBuilder builder = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                // Set the topic name(s), which is optional but recommended. It makes producer could prefetch
                // the topic route before message publishing.
                .setTopics(topic);


        //final ClientServiceProvider provider = ClientServiceProvider.loadService();


         Producer producer = builder.build();

        // Define your message body.
        byte[] body = "This is a normal message for Apache RocketMQ".getBytes(StandardCharsets.UTF_8);
        String tag = "yourMessageTagA";
        final Message message = provider.newMessageBuilder()
                // Set topic for the current message.
                .setTopic(topic)
                // Message secondary classifier of message besides topic.
                .setTag(tag)
                // Key(s) of the message, another way to mark message besides message id.
                .setKeys("yourMessageKey-1c151062f96e")
                .setBody(body)
                .build();
        try {
            final SendReceipt sendReceipt = producer.send(message);
            log.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        } catch (Throwable t) {
            log.error("Failed to send message", t);
        }

        try{
            final CompletableFuture<SendReceipt> future = producer.sendAsync(message);
            ExecutorService sendCallbackExecutor = Executors.newCachedThreadPool();
            future.whenCompleteAsync((sendReceipt, throwable) -> {
                if (null != throwable) {
                    log.error("Failed to send message", throwable);
                    // Return early.
                    return;
                }
                log.info("Send async message successfully, messageId={}", sendReceipt.getMessageId());
            }, sendCallbackExecutor);
            // Block to avoid exist of background threads.
            Thread.sleep(Long.MAX_VALUE);
        }catch (Throwable t){
            log.error("Failed to send message", t);
        }
        // Close the producer when you don't need it anymore.
        // You could close it manually or add this into the JVM shutdown hook.
       //  producer.close();
    }
}
