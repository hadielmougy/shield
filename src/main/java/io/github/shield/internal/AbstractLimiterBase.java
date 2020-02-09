package io.github.shield.internal;

import io.github.shield.Connector;
import io.github.shield.InvocationContext;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLimiterBase extends Connector {

    /**
     *
     */
    private final Semaphore semaphore;


    /**
     *
     */
    private final int permits;


    /**
     *
     */
    private long invokeTimeout;


    /**
     *
     * @param max
     * @param maxWaitMillis
     */
    public AbstractLimiterBase(int max, long maxWaitMillis) {
        this.permits = max;
        this.semaphore = new Semaphore(max, true);
        this.invokeTimeout = maxWaitMillis;
    }



    /**
     *
     * @return
     * @param context
     */
    @Override
    public boolean beforeInvocation(InvocationContext context) {
        boolean permitted = false;
        try {
            permitted = semaphore.tryAcquire(invokeTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return permitted;
    }


    public void release() {
        semaphore.release();
    }


    public void releaseAll() {
        semaphore.release(permits);
    }
}
