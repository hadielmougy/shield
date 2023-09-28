package io.github.shield;

import io.github.shield.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class InvocationContext {


    private static final String FALLBACK_SUFFIX = "Fallback";
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
    private final Supplier supplier;
    /**
     *
     */
    private Filter firstFilter;


    public InvocationContext(Filter filter, Object o, Method m, Object[] args) {
        this.firstFilter = filter;
        this.targetObject = o;
        this.targetMethod = m;
        this.args = args;
        this.supplier = targetObjectInvocation();
    }


    private Supplier targetObjectInvocation() {
        return () -> {
            try {
                return targetMethod.invoke(targetObject, args);
            } catch (InvocationCancelledException th) {
                throw th;
            } catch (Throwable th) {
                throw new InvocationException(th);
            }
        };
    }


    /**
     * @return
     */
    public Class getTargetClass() {
        return targetObject.getClass();
    }


    /**
     * @return
     */
    private String getTargetMethodName() {
        return targetMethod.getName();
    }


    /**
     * @return
     * @throws NoSuchMethodException
     */
    public Method getFallbackMethod() throws NoSuchMethodException {
        final String fallbackName = getTargetMethodName() + FALLBACK_SUFFIX;
        return getTargetClass().getDeclaredMethod(fallbackName, ClassUtil.toClassArray(args));
    }

    /**
     * @return
     */
    public Object execute() {
        return firstFilter.doInvoke(supplier);
    }


    /**
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
