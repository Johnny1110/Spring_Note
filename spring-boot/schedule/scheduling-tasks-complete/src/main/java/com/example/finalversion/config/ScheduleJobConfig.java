package com.example.finalversion.config;

import com.example.finalversion.task.BaseScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Map;
import java.util.concurrent.*;

@Configuration
@Slf4j
@EnableScheduling // 開啟排程功能
public class ScheduleJobConfig implements SchedulingConfigurer {

    private final ApplicationContext applicationContext;

    public ScheduleJobConfig(ApplicationContext applicationContext, Environment env){
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 取出 SpringApplicationContext 中所有實作 BaseScheduleTask 介面的 Spring Bean
        Map<String, BaseScheduleTask> taskMap = applicationContext.getBeansOfType(BaseScheduleTask.class);
        taskRegistrar.setScheduler(
                new GamefiScheduleJobExecutor(
                        10, // 核心 Thread 數量，供排程工作使用
                        new GamefiScheduledThreadFactory(), // 使用自訂 ThreadFactory 因為要給 Thread 命名，方便日後 debug
                        new ThreadPoolExecutor.AbortPolicy() // 使用預預設拒絕策略
                ));
        // 把所有實作 BaseScheduleTask 的任務註冊任務排程
        taskMap.forEach((k, task) -> {
            taskRegistrar.addTriggerTask(
                    // 註冊任務
                    task,
                    // 設定執行週期
                    triggerContext -> new CronTrigger(task.getCron()).nextExecutionTime(triggerContext));
        });
    }

    // 自訂 Thread工廠製造流程
    private static class GamefiScheduledThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            // 設定名稱方便 debug
            t.setName("ExampleThread-" + t.getId());
            // Thread 權限開最高，優先處理排程任務
            t.setPriority(Thread.MAX_PRIORITY);
            log.info("Thread created, name:[" + t.getName() + "]");
            return t;
        }
    }

    private static class GamefiScheduleJobExecutor extends ScheduledThreadPoolExecutor {

        public GamefiScheduleJobExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, threadFactory, handler);
        }

        // Task 開始前需要做的事前準備可以寫在這裡
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            log.info("Thread: [" + t.getName() + "] is ready to execute TASK: " + r.getClass().getName());
        }

        // Task 結束後需要做的工作或者錯誤處理可以在這裡完成
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            log.info("Task finished, TASK: " + r.getClass().getName());
            if (t != null){
                log.error("Task failed, TASK:" + r.getClass().getName(), t);
            }
        }
    }

}
