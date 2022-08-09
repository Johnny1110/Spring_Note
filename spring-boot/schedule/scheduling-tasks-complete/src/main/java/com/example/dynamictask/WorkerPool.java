package com.example.dynamictask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerPool {

    public static ExecutorService workers = Executors.newScheduledThreadPool(20);

}
