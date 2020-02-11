package io.github.shield;



/**
 * Base build type that represents that invocation of a method from a client component to the target (this)
 * The method is represented as a supplier
 */
public abstract class Filter {



    /**
     * Factory method for rate limiting build
     * @return rate limiter build factory
     */
    public static RateLimiter rateLimiter() {
        return new RateLimiter.Config();
    }

    /**
     * Factory method for throttling build
     * @return throttler build factory
     */
    public static Throttler throttler() {
        return new Throttler.Config();
    }

    /**
     * Factory method for fire and forget build
     * @return direct build factory
     */
    public static FireAndForget fireAndForget() {
        return new FireAndForget.Config();
    }


    /**
     * Factory method for Direct call build
     * @return direct build factory
     */
    public static DirectCall directCall() {
        return new DirectCall.Config();
    }

    /**
     * This should be implemented by the build type. It contains all build specific logic
     * to acquire needed resources before the invocation, like limiting requests counting requests etc.
     * @return returns the target components return value
     * @param context
     */
    public abstract boolean beforeInvocation(InvocationContext context);



    /**
     * This should be implemented by the build to close all acquired resources
     * @param context
     */
    public abstract void afterInvocation(InvocationContext context);

}
