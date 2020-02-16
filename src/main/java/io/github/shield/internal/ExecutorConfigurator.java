package io.github.shield.internal;

import io.github.shield.ExecutorProvider;


public class ExecutorConfigurator implements ExecutorProvider {


    @Override
    public void provide(final FireAndForgetFilter fireAndForgetFilter) {
        fireAndForgetFilter.setExecutorService(new BoundedExecutor());
    }

    @Override
    public void provide(final TimeoutFilter timeoutFilter) {
        timeoutFilter.setExecutorService(new BoundedExecutor());
    }
}
