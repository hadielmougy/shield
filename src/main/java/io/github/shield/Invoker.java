package io.github.shield;


import io.github.shield.internal.InvocationContext;

public interface Invoker {

    /**
     * Invoker of target object's method.
     * @param context execution context
     * @return the result of execution
     */
    Object invoke(InvocationContext context);

}
