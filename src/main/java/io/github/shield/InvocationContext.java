package io.github.shield;

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


    public Object invoke(Invocable connector) {
        return connector.doInvoke(supplier);
    }
}
