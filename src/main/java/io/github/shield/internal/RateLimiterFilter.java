package io.github.shield.internal;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 */
public class RateLimiterFilter extends AbstractLimiterBase {


    private final ScheduledExecutorService scheduler;


    /**
     *
     * @param max
     */
    public RateLimiterFilter(int max) {
        super(max, 250);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::resetPermits, 1, 1, SECONDS);
    }

    private void resetPermits() {
        releaseAll();
    }


    @Override
    public void afterInvocation() {
        // do nothing
    }
}
