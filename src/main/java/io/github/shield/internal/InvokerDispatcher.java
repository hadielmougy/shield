package io.github.shield.internal;

import io.github.shield.InvocationNotPermittedException;
import io.github.shield.Invoker;


public class InvokerDispatcher implements Invoker {

    private final TargetMethodInvoker targetMethodInvoker;
    private final FallbackMethodInvoker fallbackMethodInvoker;



    public InvokerDispatcher(TargetMethodInvoker targetMethodInvoker, FallbackMethodInvoker fallbackMethodInvoker) {
        this.targetMethodInvoker = targetMethodInvoker;
        this.fallbackMethodInvoker = fallbackMethodInvoker;
    }


    @Override
    public Object invoke(InvocationContext context) {
        Class targetClass = context.getTargetClass();
        try {
            return targetMethodInvoker.invoke(context);
        } catch (InvocationNotPermittedException ex) {
            if (isNotThrownFromTarget(ex, targetClass)) {
                return fallbackMethodInvoker.invoke(context);
            } else {
                throw ex;
            }
        }
    }

    private boolean isNotThrownFromTarget(InvocationNotPermittedException ex, Class targetClass) {
        return !ex.getThrowingClass().equals(targetClass);
    }
}
