package org.apache.rabbitmq.springboot.demo.provider;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DemoServiceImpl  {
    private static final Logger log = LoggerFactory.getLogger(DemoServiceImpl.class);
    public static void main(String[] args)   {
        // Kafka配置
        String bootstrapServers = "10.200.14.226:9092";

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 创建Kafka生产者
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // 指定主题
            String topic = "span-topic";

            // 发送消息
            for (int i = 0; i < 10; i++) {
                String key = "key" + i;
                String value = "value" + i;
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

                // 发送消息，并处理可能抛出的异常
                try {
                    RecordMetadata metadata = producer.send(record).get();
                    System.out.println("Sent: (" + metadata.partition() + ", " + metadata.offset() + ")");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 关闭生产者
            producer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

