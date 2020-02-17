package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.InvocationException;
import io.github.shield.util.ClassUtil;

import java.lang.reflect.Method;

public final class InvocationContext {



    /**
     *
     */
    private final Object targetObject;

    /**
     *
     */
    private final Method targetMethod;

    /**
     *
     */
    private final Object[] args;

    /**
     *
     */
    private Filter firstFilter;


    private static final String FALLBACK_SUFFIX = "Fallback";



    public InvocationContext(Filter filter, Object o, Method m, Object[] args) {
        this.firstFilter = filter;
        this.targetObject = o;
        this.targetMethod = m;
        this.args = args;
    }



    /**
     *
     * @return
     */
    public Class getTargetClass() {
        return targetObject.getClass();
    }


    /**
     *
     * @return
     */
    private String getTargetMethodName() {
        return targetMethod.getName();
    }


    /**
     *
     * @return
     * @throws NoSuchMethodException
     */
    public Method getFallbackMethod() throws NoSuchMethodException {
        final String fallbackName = getTargetMethodName() + FALLBACK_SUFFIX;
        return getTargetClass().getDeclaredMethod(fallbackName, ClassUtil.toClassArray(args));
    }

    /**
     *
     * @return
     */
    public Object execute() {
        return firstFilter.doInvoke();
    }


    /**
     *
     * @return
     */
    public Object executeFallback() {
        try {
            return getFallbackMethod().invoke(this.targetObject, args);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }
}
