package io.github.shield.internal;


import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLimiterBase extends AbstractBaseFilter {

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
    public AbstractLimiterBase(final int max, final long maxWaitMillis) {
        this.permits = max;
        this.semaphore = new Semaphore(max, true);
        this.invokeTimeout = maxWaitMillis;
    }



    /**
     *
     * @return
     */
    @Override
    public boolean beforeInvocation() {
        boolean permitted = false;
        try {
            permitted = semaphore.tryAcquire(
                    invokeTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return permitted;
    }


    @Override
    public Object invoke() {
        return invokeNext();
    }


    public void release() {
        semaphore.release();
    }


    public void releaseAll() {
        semaphore.release(permits);
    }
}
