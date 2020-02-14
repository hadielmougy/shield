package io.github.shield.internal;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeoutFilter extends AbstractBaseFilter {

    private final long maxWait;
    private final TimeUnit timeUnit;
    private final ScheduledExecutorService exe;
    private Thread currentThread;
    private volatile boolean running = false;

    public TimeoutFilter(long maxWait, TimeUnit timeUnit) {
        this.maxWait = maxWait;
        this.timeUnit = timeUnit;
        exe = Executors.newSingleThreadScheduledExecutor();
    }
    @Override
    public boolean beforeInvocation(InvocationContext context) {
        this.currentThread = Thread.currentThread();
        this.running = true;
        exe.scheduleWithFixedDelay(new InterruptionRunnable(), maxWait, maxWait, timeUnit);
        return true;
    }

    @Override
    public void afterInvocation(InvocationContext context) {
        running = false;
        exe.shutdownNow();
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
