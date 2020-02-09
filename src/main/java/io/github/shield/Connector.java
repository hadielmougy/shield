package io.github.shield;

import io.github.shield.internal.InvocationNotPermittedException;

import java.util.function.Supplier;


/**
 * Base connector type that represents that invocation of a method from a client component to the target (this)
 * The method is represented as a supplier
 */
public abstract class Connector {

    /**
     * Factory method for rate limiting connector
     * @return rate limiter connector factory
     */
    public static RateLimiter rateLimiter() {
        return new RateLimiter.Config();
    }

    /**
     * Factory method for throttling connector
     * @return throttler connector factory
     */
    public static Throttler throttler() {
        return new Throttler.Config();
    }

    /**
     * Factory method for Direct call connector
     * @return direct connector factory
     */
    public static DirectCall directCall() {
        return new DirectCall.Config();
    }

    /**
     * This should be implemented by the connector type. It contains all connector specific logic
     * to acquire needed resources before the invokation, like limiting requests counting requests etc.
     * @return returns the target components return value
     */
    protected abstract boolean beforeInvocation();

    /**
     * Do wrapped object invocation
     * @param supplier method call in wrapped object
     * @return wrapped object return
     */
    public final Object invoke(Supplier supplier) {
        boolean shouldInvoke = beforeInvocation();
        Object result = null;
        if (shouldInvoke) {
            try {
                result = doInvoke(supplier);
            } finally {
                afterInvocation();
            }
        } else {
            throw new InvocationNotPermittedException();
        }
        return result;
    }

    /**
     * This should be implemented by the connector to close all acquired resources
     */
    protected abstract void afterInvocation();



    /**
     * The actual invocation to the target component through this connector
     * @param supplier method call
     * @return result of invocation
     */
    private Object doInvoke(Supplier supplier) {
        return supplier.get();
    }

}
