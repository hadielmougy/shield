package io.github.shield.internal;

import io.github.shield.InvocationCancelledException;
import io.github.shield.Invoker;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class InvokerDispatcher implements Invoker {

    private final TargetMethodInvoker targetMethodInvoker;
    private final FallbackMethodInvoker fallbackMethodInvoker;



    public InvokerDispatcher(TargetMethodInvoker targetMethodInvoker, FallbackMethodInvoker fallbackMethodInvoker) {
        this.targetMethodInvoker = targetMethodInvoker;
        this.fallbackMethodInvoker = fallbackMethodInvoker;
    }


    @Override
    public Object invoke(final InvocationContext context) {
        Class targetClass = context.getTargetClass();
        try {
            return targetMethodInvoker.invoke(context);
        } catch (InvocationCancelledException ex) {
            if (isNotThrownFromTarget(ex, targetClass)) {
                return fallbackMethodInvoker.invoke(context);
            } else {
                throw ex;
            }
        }
    }

    private boolean isNotThrownFromTarget(InvocationCancelledException ex, Class targetClass) {
        return !ex.getThrowingClass().equals(targetClass);
    }
}
