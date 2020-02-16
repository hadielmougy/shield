package io.github.shield.internal;

import io.github.shield.ExecutorProvider;

import java.util.concurrent.Executors;

public class ExecutorConfigurator implements ExecutorProvider {


    @Override
    public void provide(final FireAndForgetFilter fireAndForgetFilter) {
        fireAndForgetFilter.setExecutorService(Executors.newCachedThreadPool());
    }

    @Override
    public void provide(final TimeoutFilter timeoutFilter) {
        timeoutFilter.setExecutorService(Executors.newScheduledThreadPool(4));
    }
}
