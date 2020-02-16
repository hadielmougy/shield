package io.github.shield.internal;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BoundedExecutor extends ThreadPoolExecutor {

    public BoundedExecutor() {
        super(4, 1000, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
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
