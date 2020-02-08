package io.github.shield;



public class Shield {


    public static RateLimiter rateLimiter() {
        return new RateLimiter.Config();
    }

    public static Throttler throttler() {
        return new Throttler.Config();
    }

    public static DirectCall directCall() {
        return new DirectCall.Config();
    }


}
