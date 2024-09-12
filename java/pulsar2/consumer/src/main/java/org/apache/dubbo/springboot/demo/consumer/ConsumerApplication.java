package org.apache.dubbo.springboot.demo.consumer;

import org.apache.dubbo.springboot.demo.consumer.Listener.ConsumerListener;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.TimeUnit;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;


public class ConsumerApplication {
    private static final Logger log = LoggerFactory.getLogger(ConsumerApplication.class);
    private static PulsarClient client;
    static {
        try {
// eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0ZXN0LWNvbnN1bWVyIn0.rYTReKvSqDiWQDkGn8B9MUmVGqelypM4JcK9tX5Yl20gMS_MqZL_xRNuJpdYcJHPIx9eyaBcUkXb2w94ANwtr-c3mMGBCNKNGUH1pZy4Qm7TjKcX_TDAsHHa3ZUs_h5bnSFp5TK_Hv6_K3nnB4ydbhqgmLfephlGZ3SoknCy49-ifmmNTPjTqSg2ZSwIqomOkhhgQFq7Wyt-MgG9pyUJR-fncsNkx-r3RWT3dM7laObwElcldlTAQ-Eei12AhqN2CjTXcAdp5LS_sh2LF1a4nULjKivH6Ay3nJeBcnmiB29uEyaUoX97zCRF-8aWCrf3ViwdeB4XoZcQttQnFqet0Q
            client =PulsarClient.builder()
                    .authentication(AuthenticationFactory.token("eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0ZXN0LWNvbnN1bWVyIn0.rYTReKvSqDiWQDkGn8B9MUmVGqelypM4JcK9tX5Yl20gMS_MqZL_xRNuJpdYcJHPIx9eyaBcUkXb2w94ANwtr-c3mMGBCNKNGUH1pZy4Qm7TjKcX_TDAsHHa3ZUs_h5bnSFp5TK_Hv6_K3nnB4ydbhqgmLfephlGZ3SoknCy49-ifmmNTPjTqSg2ZSwIqomOkhhgQFq7Wyt-MgG9pyUJR-fncsNkx-r3RWT3dM7laObwElcldlTAQ-Eei12AhqN2CjTXcAdp5LS_sh2LF1a4nULjKivH6Ay3nJeBcnmiB29uEyaUoX97zCRF-8aWCrf3ViwdeB4XoZcQttQnFqet0Q"))
                    .serviceUrl("pulsar://172.16.182.208:6650")
                    .build();
        }catch (PulsarClientException e) {
            throw new RuntimeException(e);
        }
    }

    private static ConsumerListener testConsumerListener=new ConsumerListener();

    public static void main(String[] args) throws PulsarClientException {

      //  testConsumerListener=new ConsumerListener();

        try {
            // ----- 单条消费
            Consumer<byte[]> consumer = client.newConsumer()
                    .topic("persistent://test/test/topic-test2")
                    .subscriptionName("test-consumer")
                    .subscriptionType(SubscriptionType.Shared)
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                    // 启动阶梯式重试，类似RocketMQ的阶梯重试，注意：否定ack，消息重试由客户端发起，而retryTopic是由broker发起
                    .messageListener(testConsumerListener)
                    .subscribe();
            Thread.sleep(1000);
            // ----- 单条消费

/*

            Consumer<byte[]> consumer = client.newConsumer()
                    .topic("persistent://test/test/topic-test2")
                    .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                    .subscriptionName("test-consumer-z")
                    .enableBatchIndexAcknowledgment(true)
                    .acknowledgmentGroupTime(10, TimeUnit.SECONDS)
                    .subscribe();

            while (true) {
                Messages<byte[]> messages = consumer.batchReceive();
                if (messages.size() == 0) {
                    System.out.println(Thread.currentThread().getContextClassLoader() + "|------No more messages available, exit the loop------|");
                    Thread.sleep(1000);
                    break;
                }
                for (Message<byte[]> message : messages) {
                    System.out.println("message");
                    log.info("message------------");
                    System.out.println(Thread.currentThread().getContextClassLoader() + "| " + new String(message.getValue()) + " |");
                }
                consumer.acknowledge(messages);
            }

            consumer.close();
            client.close();
            Thread.sleep(1000);
// --------------
*/


            log.info("init pulsar consumer success");

        } catch (PulsarClientException e) {
            log.error("topicName:[{}]初始化 pulsar consumer 失败", "persistent://public/default/TEST-TOPIC", e);
            throw new IllegalArgumentException("初始化pulsar consumer 失败");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public RestTemplate httpTemplate(){
        return new RestTemplate();
    }

}
