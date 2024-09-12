package org.apache.rabbitmq.springboot.demo.consumer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerApplication {


    @GetMapping("/")
    public String index() {
        return "index";
    }

}