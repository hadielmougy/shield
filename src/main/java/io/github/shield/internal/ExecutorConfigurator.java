package io.github.shield.internal;

import io.github.shield.ExecutorProvider;

import java.util.concurrent.Executors;


public class ExecutorConfigurator implements ExecutorProvider {


    @Override
    public void provide(final FireAndForgetFilter fireAndForgetFilter) {
        fireAndForgetFilter.setExecutorService(new BoundedExecutor(new ShieldThreadFactory()));
    }

    @Override
    public void provide(final TimeoutFilter timeoutFilter) {
        timeoutFilter.setExecutorService(new BoundedExecutor(new ShieldThreadFactory()));
    }

    @Override
    public void provide(final RateLimiterFilter rateLimiterFilter) {
        rateLimiterFilter.setExecutorService(Executors.newScheduledThreadPool(8));
    }
}
