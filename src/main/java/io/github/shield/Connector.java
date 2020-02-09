package io.github.shield;

import io.github.shield.internal.InvocationNotPermittedException;

import java.util.function.Supplier;


/**
 * Base connector type that represents that invocation of a method from a client component to the target (this)
 * The method is represented as a supplier
 */
public abstract class Connector {

    /**
     * This should be implemented by the connector type. It contains all connector specific logic
     * to acquire needed resources before the invokation, like limiting requests counting requests etc.
     * @return returns the target components return value
     */
    protected abstract boolean beforeInvocation();

    /**
     * This should be implemented by the connector to close all acquired resources
     */
    protected abstract void afterInvocation();



    public final Object invoke(Supplier supplier) {
        boolean shouldInvoke = beforeInvocation();
        Object result = null;
        if (shouldInvoke) {
            try {
                result = doInvoke(supplier);
            } finally {
                afterInvocation();
            }
        } else {
            throw new InvocationNotPermittedException();
        }
        return result;
    }

    /**
     * The actual invocation to the target component through this connector
     * @param supplier method call
     * @return result of invocation
     */
    private Object doInvoke(Supplier supplier) {
        return supplier.get();
    }

}
