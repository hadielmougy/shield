package io.github.shield.internal;

import io.github.shield.Connector;
import io.github.shield.InvocationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class FireAndForgetConnector extends Connector {

    private final ExecutorService executorService;

    public FireAndForgetConnector() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected boolean beforeInvocation(InvocationContext context) {
        Supplier supplier = context.getSupplier();
        context.setSupplier(() -> executorService.submit(() -> supplier.get()));
        return true;
    }

    @Override
    protected void afterInvocation(InvocationContext context) {
        executorService.shutdown();
    }
}
