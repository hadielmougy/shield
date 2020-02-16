package io.github.shield.internal;

import io.github.shield.InvocationException;
import io.github.shield.Invoker;

public class FallbackMethodInvoker implements Invoker {



    @Override
    public Object invoke(InvocationContext context) {
        try {
            return context.executeFallback();
        } catch (InvocationException e) {
            System.err.printf("Error invoking fallback method with cause %s", e.getMessage());
        }
        return null;
    }


}
