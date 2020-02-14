package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.Invoker;
import io.github.shield.InvocationNotPermittedException;
import io.github.shield.InvocationException;

public class TargetMethodInvoker implements Invoker {




    @Override
    public Object invoke(InvocationContext context) {
        beforeInvocation(context);

        try {
            return context.invoke();
        } catch (InvocationNotPermittedException ex) {
            throw ex;
        } catch (InvocationException ex) {
            ex.printStackTrace();
        } finally {
            afterInvocation(context);
        }
        return null;
    }

    private void beforeInvocation(InvocationContext context) {
        for (Filter conn : context.getFilters()) {
            boolean shouldContinue = conn.beforeInvocation(context);
            if (!shouldContinue) {
                throw new InvocationNotPermittedException(context.getTargetObject().getClass());
            }
        }
    }


    private void afterInvocation(InvocationContext context) {
        context.getFilters().stream()
                .forEach(conn -> conn.afterInvocation(context));
    }
}
