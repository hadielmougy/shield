package io.github.shield;

import java.util.function.Supplier;


/**
 *
 */
public abstract class Connector {

    /**
     *
     * @param supplier
     * @return
     */
    public abstract Object invoke(Supplier supplier);

    /**
     *
     * @param supplier
     * @return
     */
    protected Object doInvoke(Supplier supplier) {
        return supplier.get();
    }

}
