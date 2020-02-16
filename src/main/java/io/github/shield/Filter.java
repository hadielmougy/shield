package io.github.shield;


import io.github.shield.internal.InvocationContext;

/**
 * Base build type that represents that invocation of a method from a client
 * component to the target (this).
 * The method is represented as a supplier
 */
public interface Filter extends Comparable<Filter> {



    /**
     * Factory method for rate limiting build.
     * @return rate limiter build factory
     */
    static RateLimiter rateLimiter() {
        return new RateLimiter.Config();
    }

    /**
     * Factory method for throttling build.
     * @return throttler build factory
     */
    static Throttler throttler() {
        return new Throttler.Config();
    }

    /**
     * Factory method for fire and forget build.
     * @return direct build factory
     */
    static FireAndForget fireAndForget() {
        return new FireAndForget.Config();
    }


    /**
     * Factory method for retry build.
     * @return direct build factory
     */
    static Retry retry() {
        return new Retry.Config();
    }


    /**
     * This should be implemented by the build type. It contains all build
     * specific logic to acquire needed resources before the invocation,
     * like limiting requests counting requests etc.
     * @return returns the target components return value
     */
    boolean beforeInvocation();



    /**
     * This should be implemented by the build to close all acquired resources.
     */
    void afterInvocation();

    /**
     * order of this filter instance in the execution chain.
     * @return the number that indicates rhe order
     */
    Integer getOrder();


    /**
     * Next filter of execution chain.
     * @param next next filter
     */
    void setNext(Filter next);


    /**
     * Invoke this filter.
     * @return result of execution
     */
    Object invoke();


    /**
     * Add execution context to the filter instance.
     * @param context execution context
     */
    void setContext(InvocationContext context);
}
