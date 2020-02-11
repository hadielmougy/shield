package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.Invocation;

public class TargetMethodInvocation implements Invocation {




    @Override
    public Object invoke(InvocationContext context) {
        beforeInvocation(context);

        try {
            return context.getSupplier().get();
        } finally {
            afterInvocation(context);
        }
    }

    private void beforeInvocation(InvocationContext context) {
        for (Filter conn : context.getFilters()) {
            boolean toContinue = conn.beforeInvocation(context);
            if (!toContinue) {
                throw new InvocationNotPermittedException(context.getTargetObject().getClass());
            }
        }
    }


    private void afterInvocation(InvocationContext context) {
        context.getFilters().stream()
                .forEach(conn -> conn.afterInvocation(context));
    }
}
