package io.github.shield;

import java.util.function.Supplier;



public abstract class Connector {

    public abstract Object invoke(Supplier supplier);

    protected Object doInvoke(Supplier supplier) {
        return supplier.get();
    }

}
