package io.github.shield.internal;

import io.github.shield.Invocation;
import io.github.shield.util.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FallbackMethodInvocation implements Invocation {



    @Override
    public Object invoke(InvocationContext context) {
        String fallbackName = context.getTargetMethod().getName() + "Fallback";
        Method fallback = null;
        Object result = null;
        try {
            fallback = context.getTargetObject().getClass().getDeclaredMethod(fallbackName, ClassUtil.toClassArray(context.getArgs()));
        } catch (NoSuchMethodException e) {
            System.err.printf("No fallback is found for %s", context.getTargetMethod().getName());
        }
        try {
            if (fallback != null) {
                result = fallback.invoke(context.getTargetObject(), context.getArgs());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.printf("Error invoking fallback method for for %s", fallbackName);
        }
        return result;
    }
}
