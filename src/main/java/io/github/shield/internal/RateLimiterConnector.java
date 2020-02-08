package io.github.shield.internal;

import com.google.common.util.concurrent.RateLimiter;
import io.github.shield.Connector;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 *
 */
public class RateLimiterConnector extends Connector {


    /**
     *
     */
    private final RateLimiter rateLimiter;


    /**
     *
     * @param rate
     */
    public RateLimiterConnector(Double rate) {
        rateLimiter = RateLimiter.create(rate);
    }


    /**
     *
     * @param supplier
     * @return
     */
    @Override
    public Object invoke(Supplier supplier) {
        boolean permitted = rateLimiter.tryAcquire(250, TimeUnit.MILLISECONDS);
        Object result = null;
        if (permitted) {
            result = doInvoke(supplier);
        }
        return result;
    }
}
