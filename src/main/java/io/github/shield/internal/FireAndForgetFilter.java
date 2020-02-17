package io.github.shield.internal;

import io.github.shield.ExecutorProvider;

import java.util.function.Supplier;


public
class FireAndForgetFilter extends AbstractBaseFilter {


    /**
     * This called before the object being used to get the executor
     * implementation based on implementation type.
     * @param executorProvider
     */
    @Override
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
     * @param supplier
     */
    @Override
    public Object invoke(Supplier supplier) {
        ensureExecutor();
        InvocationContext context = getContext();
        executorService.submit(() -> {
            // copy context to the new thread
            setContext(context);
            return invokeNext(supplier);
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
