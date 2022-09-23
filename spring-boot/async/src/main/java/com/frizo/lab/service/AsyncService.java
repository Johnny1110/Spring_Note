package com.frizo.lab.service;

import com.frizo.lab.entity.DeliverItemOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AsyncService {

    private static Logger logger = LoggerFactory.getLogger(AsyncService.class);

    @Async("async_executor")//指定配置的 executor Bean
    public void configName(){
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("configName，Thread：" + Thread.currentThread().getName());
    }

    @Async //透過 getAsyncExecutor 方法找出配置的預設 executor Bean
    public Future<String> noConfigName(){
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("noCofigName，Thread：" + Thread.currentThread().getName());
        return new AsyncResult<>("noCofigName，Thread：" + Thread.currentThread().getName());

    }



}
