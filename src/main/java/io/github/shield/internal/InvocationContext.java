package io.github.shield.internal;

import java.util.function.Supplier;

public final class InvocationContext {

    private Supplier supplier;


    public InvocationContext(Supplier supplier) {
        this.supplier = supplier;
    }

    public synchronized Supplier getSupplier() {
        return supplier;
    }

    public synchronized void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

}
