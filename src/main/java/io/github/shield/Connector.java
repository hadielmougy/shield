package io.github.shield;

import io.github.shield.internal.InvocationNotPermittedException;

import java.util.function.Supplier;


/**
 * Base connector type that represents that invocation of a method from a client component to the target (this)
 * The method is represented as a supplier
 */
public abstract class Connector implements Invocable {



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
     * Factory method for fire and forget connector
     * @return direct connector factory
     */
    public static FireAndForget fireAndForget() {
        return new FireAndForget.Config();
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
     * to acquire needed resources before the invocation, like limiting requests counting requests etc.
     * @return returns the target components return value
     * @param context
     */
    protected abstract boolean beforeInvocation(InvocationContext context);

    /**
     * Do wrapped object invocation
     * @return wrapped object return
     */
    public final Object invoke(Supplier supplier) {
        InvocationContext context = new InvocationContext(supplier);
        boolean shouldInvoke = beforeInvocation(context);
        Object result = null;
        if (shouldInvoke) {
            try {
                result = context.invoke(this);
            } finally {
                afterInvocation(context);
            }
        } else {
            throw new InvocationNotPermittedException();
        }
        return result;
    }

    /**
     * This should be implemented by the connector to close all acquired resources
     * @param context
     */
    protected abstract void afterInvocation(InvocationContext context);



    /**
     * The actual invocation to the target component through this connector
     * @param supplier method call
     * @return result of invocation
     */
    @Override
    public Object doInvoke(Supplier supplier) {
        return supplier.get();
    }

}
