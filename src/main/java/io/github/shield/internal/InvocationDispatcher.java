package io.github.shield.internal;

import io.github.shield.Invocation;


public class InvocationDispatcher implements Invocation {

    private final TargetMethodInvocation targetMethodInvocation;
    private final FallbackMethodInvocation fallbackMethodInvocation;

    public InvocationDispatcher(TargetMethodInvocation targetMethodInvocation,
                                FallbackMethodInvocation fallbackMethodInvocation) {
        this.targetMethodInvocation = targetMethodInvocation;
        this.fallbackMethodInvocation = fallbackMethodInvocation;
    }
    @Override
    public Object invoke(InvocationContext context) {
        Class targetClass = context.getTargetObject().getClass();
        try {
            return targetMethodInvocation.invoke(context);
        } catch (InvocationNotPermittedException ex) {
            if (isNotFromTarget(ex, targetClass)) {
                return fallbackMethodInvocation.invoke(context);
            } else {
                throw ex;
            }
        }
    }

    private boolean isNotFromTarget(InvocationNotPermittedException ex, Class targetClass) {
        return !ex.getThrowingClass().equals(targetClass);
    }
}
