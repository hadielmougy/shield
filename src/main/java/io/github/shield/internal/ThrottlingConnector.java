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
    private final Semaphore permits;

    /**
     *
     */
    private long invokeTimeout;


    /**
     *
     * @param max
     * @param maxWaitMillis
     */
    public ThrottlingConnector(int max, long maxWaitMillis) {
        this.permits = new Semaphore(max, true);
        invokeTimeout = maxWaitMillis;
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
            permitted = permits.tryAcquire(invokeTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object result = null;
        if (permitted) {
            try {
                result = doInvoke(supplier);
            } finally {
                permits.release();
            }
        } else {
            throw new InvocationNotPermittedException();
        }
        return result;
    }
}
