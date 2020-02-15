package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.Invocable;
import io.github.shield.InvocationException;
import io.github.shield.InvocationNotPermittedException;
import io.github.shield.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public final class InvocationContext {

    private Invocable invocable;

    private final List<Filter> filters;
    private final Object targetObject;
    private final Method targetMethod;
    private final Object[] args;


    private static final String FALLBACK_SUFFIX = "Fallback";


    public InvocationContext(List<Filter> filters,
                             Object targetObject,
                             Method targetMethod,
                             Object[] args) {
        this.filters = filters;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.args = args;

        initInvocable();
    }

    private void initInvocable() {
        this.invocable = () -> {
            try {
                return targetMethod.invoke(targetObject, args);
            } catch (InvocationNotPermittedException th) {
                throw th;
            } catch (Throwable th) {
                throw new InvocationException(th);
            }
        };
    }


    public Invocable getInvocable() {
        return invocable;
    }

    public void setInvocable(Invocable invocable) {
        this.invocable = invocable;
    }

    public List<Filter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getArgs() {
        return args;
    }


    public Class getTargetClass() {
        return getTargetObject().getClass();
    }

    public String getTargetMethodName() {
        return getTargetMethod().getName();
    }

    public Method getFallbackMethod() throws NoSuchMethodException {
        final String fallbackName = getTargetMethodName() + FALLBACK_SUFFIX;
        return getTargetClass().getDeclaredMethod(fallbackName, ClassUtil.toClassArray(getArgs()));
    }

    public Object invoke() {
        return getInvocable().invoke();
    }

    public Object invokeFallback() {
        try {
            return getFallbackMethod().invoke(this.targetObject, this.getArgs());
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }
}
