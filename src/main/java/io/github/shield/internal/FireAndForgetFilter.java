package io.github.shield.internal;

import io.github.shield.Filter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class FireAndForgetFilter implements Filter {

    private final ExecutorService executorService;

    public FireAndForgetFilter() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public boolean beforeInvocation(InvocationContext context) {
        Supplier supplier = context.getSupplier();
        context.setSupplier(() -> executorService.submit(() -> supplier.get()));
        return true;
    }

    @Override
    public void afterInvocation(InvocationContext context) {
        executorService.shutdown();
    }
}
