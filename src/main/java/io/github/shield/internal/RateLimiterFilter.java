package io.github.shield.internal;


import io.github.shield.ExecutorAware;
import io.github.shield.ExecutorProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 */
public class RateLimiterFilter extends AbstractLimiterBase implements ExecutorAware {


    private ScheduledExecutorService scheduler;


    /**
     *
     * @param max
     */
    public RateLimiterFilter(final int max) {
        super(max, 250);
    }

    private void resetPermits() {
        releaseAll();
    }


    @Override
    public void afterInvocation() {
        // do nothing
    }

    @Override
    public void configureExecutor(final ExecutorProvider provider) {
        provider.provide(this);
    }

    @Override
    public void setExecutorService(final ExecutorService executorService) {
        this.scheduler = (ScheduledExecutorService) executorService;
        scheduler.scheduleAtFixedRate(this::resetPermits, 1, 1, SECONDS);
    }
}
