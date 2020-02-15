package io.github.shield.internal;

import io.github.shield.Invocable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FireAndForgetFilter extends AbstractBaseFilter {

    private final ExecutorService executorService;

    public FireAndForgetFilter() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public boolean beforeInvocation(final InvocationContext context) {
        final Invocable invocable = context.getInvocable();
        context.setInvocable(() -> executorService.submit(() -> invocable.invoke()));
        return true;
    }

    @Override
    public void afterInvocation(final InvocationContext context) {
        executorService.shutdown();
    }
}
