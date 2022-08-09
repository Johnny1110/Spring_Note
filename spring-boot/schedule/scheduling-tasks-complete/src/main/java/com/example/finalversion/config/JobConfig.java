package com.example.finalversion.config;

import com.example.dynamictask.WorkerPool;
import com.example.finalversion.task.BaseTask;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@Configuration
public class JobConfig implements SchedulingConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ExecutorService scheduledWorkerPool;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 取出 applicationContext 中所有 BaseTask 類的 Spring Bean (TaskOne TaskTwo)
        Map<String, BaseTask> taskMap = applicationContext.getBeansOfType(BaseTask.class);
        taskRegistrar.setScheduler(scheduledWorkerPool);
        taskMap.forEach((k, v) -> {
            taskRegistrar.addTriggerTask(v,
                    // 設定執行週期
                    triggerContext -> {
                        // 取得 corn
                        String cron = v.getCron();
                        //
                        // 返回 CronTrigger
                        return new CronTrigger(cron).nextExecutionTime(triggerContext);
                    });
        });

    }
}
