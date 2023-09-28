package io.github.shield;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BoundedExecutor extends ThreadPoolExecutor {

    public BoundedExecutor(final ShieldThreadFactory shieldThreadFactory) {
        super(4,
                100,
                1,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                shieldThreadFactory);
    }


    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
    }


    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {
        super.afterExecute(r, t);
    }


}
