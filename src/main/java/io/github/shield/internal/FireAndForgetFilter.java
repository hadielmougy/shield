package io.github.shield.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FireAndForgetFilter extends AbstractBaseFilter {

    private final ExecutorService executorService;

    public FireAndForgetFilter() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public boolean beforeInvocation() {
        return true;
    }

    @Override
    public Object invoke() {
        executorService.submit(() -> invokeNext());
        return null;
    }

    @Override
    public void afterInvocation() {
        executorService.shutdown();
    }



}
