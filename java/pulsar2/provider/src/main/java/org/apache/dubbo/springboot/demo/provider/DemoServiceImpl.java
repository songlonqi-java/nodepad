package org.apache.dubbo.springboot.demo.provider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoServiceImpl {
    public static void main(String[] args)  {
        SpringApplication.run(DemoServiceImpl.class, args);

    }
}
