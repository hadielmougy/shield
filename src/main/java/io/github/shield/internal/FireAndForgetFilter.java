package io.github.shield.internal;

import io.github.shield.ExecutorProvider;
import io.github.shield.ExecutorAware;

import java.util.concurrent.ExecutorService;

public
class FireAndForgetFilter extends AbstractBaseFilter implements ExecutorAware {


    /**
     *
     */
    private ExecutorService executorService;


    /**
     * This called before the object being used to provide the executor
     * implementation based on implementation type.
     * @param executorProvider
     */
    public void configureExecutor(final ExecutorProvider executorProvider) {
        executorProvider.provide(this);
    }


    /**
     *
     * @param exe
     */
    public void setExecutorService(final ExecutorService exe) {
        this.executorService = exe;
    }

    /**
     * Called before invocation.
     */
    @Override
    public boolean beforeInvocation() {
        return true;
    }


    /**
     * Execute invocation chain.
     */
    @Override
    public Object invoke() {
        ensureExecutor();
        executorService.submit(() -> invokeNext());
        return null;
    }


    /**
     * Called after invoke to release used resources if any.
     */
    @Override
    public void afterInvocation() {
        ensureExecutor();
        executorService.shutdown();
    }


    private void ensureExecutor() {
        if (executorService == null) {
            final String msg = "executor service is not configured";
            throw new IllegalStateException(msg);
        }
    }



}
