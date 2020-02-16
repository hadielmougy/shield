package io.github.shield.internal;

import io.github.shield.Invoker;

public class TargetMethodInvoker implements Invoker {

    @Override
    public Object invoke(InvocationContext context) {
        return context.invoke();
    }

}
