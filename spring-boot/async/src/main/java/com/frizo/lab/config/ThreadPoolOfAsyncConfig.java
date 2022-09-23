package com.frizo.lab.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync // 開啟非同步支持
public class ThreadPoolOfAsyncConfig implements AsyncConfigurer {
    private static final int CORE_POLL_SIZE = 100;

    private static final int MAX_POLL_SIZE = 200;

    private static final int QUEUE_CAPACITY = 1;

    private static final int KEEP_ALIVE_SECONDS = 60;

    @Bean("async_executor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POLL_SIZE);
        taskExecutor.setMaxPoolSize(MAX_POLL_SIZE);
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);//當 ThreadPool 關閉時，等待所有任務都完成再銷會其他bean
        taskExecutor.setAwaitTerminationSeconds(KEEP_ALIVE_SECONDS);//設定 ThreadPool 關閉時的任務等待時間，如果超出時間沒有銷毀則強制鄉毀，以確保能關閉，而不是關不掉
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//拒絕策略
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
