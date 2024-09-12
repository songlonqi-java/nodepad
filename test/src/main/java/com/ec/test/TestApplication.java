package com.ec.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;

@SpringBootApplication
@Import(cn.hutool.extra.spring.SpringUtil.class)

@Slf4j
public class TestApplication implements ApplicationListener<ContextRefreshedEvent> {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info(event.toString());
    }
}
