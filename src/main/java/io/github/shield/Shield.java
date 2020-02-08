package io.github.shield;


/**
 *
 */
public class Shield {


    /**
     *
     * @return
     */
    public static RateLimiter rateLimiter() {
        return new RateLimiter.Config();
    }


    /**
     *
     * @return
     */
    public static Throttler throttler() {
        return new Throttler.Config();
    }


    /**
     *
     * @return
     */
    public static DirectCall directCall() {
        return new DirectCall.Config();
    }


}
