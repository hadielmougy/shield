package io.github.shield.internal;

import io.github.shield.ExecutorAware;
import io.github.shield.ExecutorProvider;

import java.util.concurrent.ExecutorService;

public
class FireAndForgetFilter extends AbstractBaseFilter implements ExecutorAware {


    /**
     *
     */
    private ExecutorService executorService;


    /**
     * This called before the object being used to get the executor
     * implementation based on implementation type.
     * @param executorProvider
     */
    public void configureExecutor(final ExecutorProvider executorProvider) {
        executorService = executorProvider.get(this);
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
        InvocationContext context = getContext();
        executorService.submit(() -> {
            // copy context to the new thread
            setContext(context);
            return invokeNext();
        });
        return null;
    }


    /**
     * Called after execute to release used resources if any.
     */
    @Override
    public void afterInvocation() {
    }


    private void ensureExecutor() {
        if (executorService == null) {
            final String msg = "executor service is not configured";
            throw new IllegalStateException(msg);
        }
    }



}
