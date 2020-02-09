package io.github.shield.internal;


import io.github.shield.InvocationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 */
public class RateLimiterConnector extends AbstractLimiterBase {


    private final ScheduledExecutorService scheduler;


    /**
     *
     * @param max
     */
    public RateLimiterConnector(int max, Object target) {
        super(max, 250, target);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::resetPermits, 1, 1, SECONDS);
    }

    private void resetPermits() {
        releaseAll();
    }


    @Override
    public void afterInvocation(InvocationContext context) {
        // do nothing
    }
}
