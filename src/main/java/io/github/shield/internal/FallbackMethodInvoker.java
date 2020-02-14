package io.github.shield.internal;

import io.github.shield.Invoker;
import io.github.shield.InvocationException;

public class FallbackMethodInvoker implements Invoker {



    @Override
    public Object invoke(InvocationContext context) {
        try {
            return context.invokeFallback();
        } catch (InvocationException e) {
            System.err.printf("Error invoking fallback method with cause %s", e.getMessage());
        }
        return null;
    }


}
