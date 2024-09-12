package org.apache.dubbo.springboot.demo.provider.controller;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.impl.MessagesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;

@Controller
public class PulsarController {
    private static PulsarClient client;

    static {
        try {
            client = PulsarClient.builder()
                    .authentication(AuthenticationFactory.token("eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXByb2R1Y2VyIn0.XO-i0tKAUJx_T2UYZS-qolopFZ8kLzVGvcsZ551eIqQwie2ET6S71nIMlm-x_C2P84fuZOLgJZDaPv3viJLe1-6Exf0gDGXMzS0FYT_qj3cRPQF6zjatf3fNc2_sWaMROBLzfyAzN-vrkaYJ90yXbQaIHPXWHZxQo5qR8rlxMjVEysnkjb1BKfjrCfL8hDGfvOCIVo7gJnnrWM4dHz3t3OkH-cEUb5Hv8jhcDFqUVwBjM9toOtyMxnuNIc57arZzo_AubA-I6a7JaNa28DqHQ2vPzuhC5xRQpYR9eiw6NZ-c71hdiToESVPaCcRSeCS7mhqDKGBlN6kIx_87dAFSPA"))
                        .serviceUrl("pulsar://172.16.182.208:6650")
                        .build();
        } catch (PulsarClientException e) {
            throw new RuntimeException(e);
        }
    };

    private static final String TOPIC_NAME = "persistent://test/test/topic-test2";
    private static final Logger logger = LoggerFactory.getLogger(PulsarController.class);


    @GetMapping("/send")
    @ResponseBody
    public String client()  {
        logger.info("this is 客户端");
        try {
            Producer<byte[]> producer = client.newProducer()
                    .topic(TOPIC_NAME).producerName("test-producer")
                    .compressionType(CompressionType.LZ4)
                    .create();

           // producer.newMessage().key("this is key").value("{this is msg}".getBytes(StandardCharsets.UTF_8)).send();
            for (int i = 0; i < 5; i++) {
                String message = "Message: " + i;
                producer.newMessage()
                        .value(message.getBytes())
                        .send();
            }

            Thread.sleep(1000);
            System.out.println("发送成功");
            producer.close();
        }catch (Exception e){
            System.out.println("发送失败  err"+e.getMessage());
        }

        return "this is 客户端";
    }
}
