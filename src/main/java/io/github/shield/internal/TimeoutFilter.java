package io.github.shield.internal;


import io.github.shield.ExecutorProvider;
import io.github.shield.ExecutorAware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeoutFilter extends AbstractBaseFilter implements ExecutorAware {

    /**
     */
    private final long maxWait;
    /**
     */
    private final TimeUnit timeUnit;
    /**
     */
    private ScheduledExecutorService exe;
    /**
     */
    private Thread currentThread;
    /**
     */
    private volatile boolean running = false;

    /**
     * {@inheritDoc}.
     * @param wait
     * @param unit
     */
    public TimeoutFilter(final long wait, final TimeUnit unit) {
        this.maxWait = wait;
        this.timeUnit = unit;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean beforeInvocation() {
        this.currentThread = Thread.currentThread();
        this.running = true;
        exe.scheduleWithFixedDelay(new InterruptionRunnable(), maxWait, maxWait, timeUnit);
        return true;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Object invoke() {
        return invokeNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterInvocation() {
        running = false;
        exe.shutdownNow();
    }

    /**
     * {@inheritDoc}
     * @param executorProvider
     */
    @Override
    public void configureExecutor(final ExecutorProvider executorProvider) {
        executorProvider.provide(this);
    }

    /**
     * {@inheritDoc}
     * @param executor
     */
    @Override
    public void setExecutorService(final ExecutorService executor) {
        this.exe = (ScheduledExecutorService) executor;
    }

    private class InterruptionRunnable implements Runnable {
        @Override
        public void run() {
            if (running) {
                currentThread.interrupt();
                exe.shutdownNow();
            }
        }
    }

}
