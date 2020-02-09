package io.github.shield.internal;

import io.github.shield.Connector;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 *
 */
public class ThrottlingConnector extends Connector {

    /**
     *
     */
    protected final Semaphore semaphore;


    protected final int permits;
    protected final boolean releasable;

    /**
     *
     */
    protected long invokeTimeout;


    /**
     *
     * @param max
     * @param maxWaitMillis
     */
    public ThrottlingConnector(int max, long maxWaitMillis) {
        this(max, maxWaitMillis, true);
    }


    /**
     *
     * @param max
     * @param maxWaitMillis
     */
    public ThrottlingConnector(int max, long maxWaitMillis, boolean releasable) {
        this.permits = max;
        this.semaphore = new Semaphore(max, true);
        this.invokeTimeout = maxWaitMillis;
        this.releasable = releasable;

    }


    /**
     *
     * @param supplier
     * @return
     */
    @Override
    public Object invoke(Supplier supplier) {
        boolean permitted = false;
        try {
            permitted = semaphore.tryAcquire(invokeTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object result = null;
        if (permitted) {
            try {
                result = doInvoke(supplier);
            } finally {
                if (releasable)
                    semaphore.release();
            }
        } else {
            throw new InvocationNotPermittedException();
        }
        return result;
    }
}
