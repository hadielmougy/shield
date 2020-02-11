package io.github.shield;


import io.github.shield.internal.InvocationContext;

/**
 * Base build type that represents that invocation of a method from a client component to the target (this)
 * The method is represented as a supplier
 */
public interface Filter {



    /**
     * Factory method for rate limiting build
     * @return rate limiter build factory
     */
    static RateLimiter rateLimiter() {
        return new RateLimiter.Config();
    }

    /**
     * Factory method for throttling build
     * @return throttler build factory
     */
    static Throttler throttler() {
        return new Throttler.Config();
    }

    /**
     * Factory method for fire and forget build
     * @return direct build factory
     */
    static FireAndForget fireAndForget() {
        return new FireAndForget.Config();
    }


    /**
     * Factory method for Direct call build
     * @return direct build factory
     */
    static DirectCall directCall() {
        return new DirectCall.Config();
    }

    /**
     * This should be implemented by the build type. It contains all build specific logic
     * to acquire needed resources before the invocation, like limiting requests counting requests etc.
     * @return returns the target components return value
     * @param context
     */
    boolean beforeInvocation(InvocationContext context);



    /**
     * This should be implemented by the build to close all acquired resources
     * @param context
     */
    void afterInvocation(InvocationContext context);

}
