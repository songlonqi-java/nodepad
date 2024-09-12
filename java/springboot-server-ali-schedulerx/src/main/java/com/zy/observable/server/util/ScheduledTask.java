package com.zy.observable.server.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
    @Scheduled(initialDelay=1000, fixedDelay = 2000)
    public void task1() {
        System.out.println("延迟1000毫秒后执行，任务执行完2000毫秒之后执行！ task1");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Scheduled(fixedRate = 2000)
    public void task2() {
        System.out.println("延迟1000毫秒后执行，之后每2000毫秒执行一次！task2");
    }
}