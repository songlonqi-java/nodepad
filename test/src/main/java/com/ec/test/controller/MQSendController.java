package com.ec.test.controller;

import com.ec.test.mq.producer.PulsarProducerFactory;
import com.ec.test.vo.OnceMqVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**

 **/

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mq")
public class MQSendController {

    private final PulsarProducerFactory pulsarProducerFactory;

    @GetMapping("/sendOnce")
    public String sendMqOnce() {
        log.info("test msg");
        // 发送一次性单条消息
        pulsarProducerFactory.sendMsgOnce("TEST-TOPIC", "getMsgBody", "onceMqVo");
        return "Success()";
    }

    @PostMapping("/sendBatchOnce")
    public String sendMqBatchOnce(@RequestBody List<OnceMqVo> onceMqVoList) {
        return "Success()";
    }

}
