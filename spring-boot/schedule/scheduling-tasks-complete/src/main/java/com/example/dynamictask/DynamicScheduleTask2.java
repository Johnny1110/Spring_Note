package com.example.dynamictask;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

//@Configuration
//@EnableScheduling
public class DynamicScheduleTask2 implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(WorkerPool.workers); // 不加這一段會變成單線程
        taskRegistrar.addTriggerTask(
                // 添加任務內容
                () -> {
                    System.out.println("執行動態定時任務2: " + LocalDateTime.now().toLocalTime());
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },

                // 設定執行週期
                triggerContext -> {
                    // 取得 corn
                    String cron = "0/5 * * * * ?"; // 可以從 DB 裡讀取 corn
                    // 驗證合法性
                    if (StringUtils.isEmpty(cron)) {
                        // Omitted Code ..
                    }
                    //2.3 返回 CronTrigger
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );
    }

}

