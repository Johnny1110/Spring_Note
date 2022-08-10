package com.example.finalversion.task;

import com.example.finalversion.mapper.CronMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskOne implements BaseScheduleTask {

    @Autowired
    CronMapper cronMapper;

    @Override
    public String getCron() {
        return cronMapper.getCronById(1);
    }

    @Override
    public void run() {
        System.out.println("執行動態定時任務-1: " + LocalDateTime.now().toLocalTime());
        try {
            // 模擬耗時操作
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
