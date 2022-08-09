package com.example.multithreadtask;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


//@Component
@EnableAsync // 開啟多執行緒
public class MultithreadScheduleTask {

    @Async
    @Scheduled(fixedDelay = 1000)  //間隔1秒
    public void first() throws InterruptedException {
        System.out.println("Task-1 : " + LocalDateTime.now().toLocalTime() + "\r\nThread: " + Thread.currentThread().getName());
        System.out.println();
        Thread.sleep(1000 * 10); // 暫停一秒
    }

    @Async
    @Scheduled(fixedDelay = 2000)
    public void second() {
        System.out.println("Task-2 : " + LocalDateTime.now().toLocalTime() + "\r\nThread : " + Thread.currentThread().getName());
        System.out.println();
    }

    // 理論上兩個 Task 都會同時執行
}
