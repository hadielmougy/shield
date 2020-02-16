package io.github.shield.internal;

import io.github.shield.ExecutorProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExecutorConfigurer implements ExecutorProvider {



    @Override
    public ExecutorService get(final FireAndForgetFilter fireAndForgetFilter) {
        return new BoundedExecutor(new ShieldThreadFactory());
    }

    @Override
    public ExecutorService get(final TimeoutFilter timeoutFilter) {
        return new BoundedExecutor(new ShieldThreadFactory());
    }

    @Override
    public ExecutorService get(final RateLimiterFilter rateLimiterFilter) {
        return Executors.newScheduledThreadPool(8);
    }
}
