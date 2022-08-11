package com.example.finalversion.task;

import com.example.finalversion.mapper.CronMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskTwo implements BaseScheduleTask {

    @Autowired
    CronMapper cronMapper;

    @Override
    public String getCron() {
        return cronMapper.getCronById(2);
    }

    @Override
    public void run() {
        System.out.println("執行動態定時任務-2: " + LocalDateTime.now().toLocalTime());
        System.out.println(100/0); // 模擬噴錯，要被捕捉到才可以。
        try {
            // 模擬耗時操作
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
