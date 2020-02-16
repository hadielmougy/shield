package io.github.shield.internal;


import io.github.shield.ExecutorProvider;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeoutFilter extends AbstractBaseFilter {

    /**
     */
    private final long maxWait;
    /**
     */
    private final TimeUnit timeunit;
    /**
     */
    private ExecutorService exe;

    /**
     * {@inheritDoc}.
     * @param wait
     * @param unit
     */
    public TimeoutFilter(final long wait, final TimeUnit unit) {
        this.maxWait = wait;
        this.timeunit = unit;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean beforeInvocation() {
        return true;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Object invoke() {
        InvocationContext context = getContext();
        Future<Object> future = exe.submit(() -> {
            // copy context to the new thread
            setContext(context);
            return invokeNext();
        });
        try {
            return future.get(maxWait, timeunit);
        } catch (TimeoutException ex) {
            // handle the timeout
        } catch (InterruptedException e) {
            // handle the interrupts
        } catch (ExecutionException e) {
            // handle other exceptions
        } finally {
            future.cancel(true);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterInvocation() {
    }

    /**
     * {@inheritDoc}
     * @param executorProvider
     */
    @Override
    public void configureExecutor(final ExecutorProvider executorProvider) {
        this.exe = executorProvider.get(this);
    }


}
