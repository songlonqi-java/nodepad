package org.apache.rabbitmq.springboot.demo.consumer;

import org.apache.rabbitmq.springboot.demo.consumer.controller.ServerApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ConsumerApplication {
   // private static final Logger log = LoggerFactory.getLogger(ConsumerApplication.class);

    public static void main(String[] args)    {
        // 配置Consumer
/*        Properties props = new Properties();
        props.put("bootstrap.servers", "10.200.14.226:9092");
        props.put("group.id", "test-group");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        try{
            consumer.subscribe(Arrays.asList("span-topic"));

            // 循环消费消息
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Offset: %d, Key: %s, Value: %s\n", record.offset(), record.key(), record.value());
                }
            }
        }catch (Exception e){
            log.error(e.toString());
        }*/

        SpringApplication.run(ServerApplication.class, args);
    }
}

