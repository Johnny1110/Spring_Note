package com.example.finalversion.task;

import java.util.concurrent.*;

public class DemoFutureTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
    public DemoFutureTask(Callable<V> callable) {
        super(callable);
    }

    public DemoFutureTask(Runnable runnable, V result) {
        super(runnable, result);
    }

    @Override
    public boolean isPeriodic() {
        return false;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return 0;
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}
