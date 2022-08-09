package com.example.schedulingtask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


//@Component // 標註起來就會被 SpringContext 當成 Spring Bean 管理
public class ScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000) // 每 5 秒執行一次
    public void reportCurrentTime1() throws InterruptedException {
        Thread.sleep(5000L);
        log.info("Now time1 is: {}", dateFormat.format(new Date()));
    }

    @Scheduled(fixedRate = 5000) // 每 5 秒執行一次
    public void reportCurrentTime2() throws InterruptedException {
        Thread.sleep(5000L);
        log.info("Now time2 is: {}", dateFormat.format(new Date()));
    }
}