package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.InvocationException;
import io.github.shield.InvocationCancelledException;
import io.github.shield.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public final class InvocationContext {


    /**
     *
     */
    private final List<Filter> filters;

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



    public InvocationContext(List<Filter> filters, Object targetObject, Method targetMethod, Object[] args) {
        this.filters = filters;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.args = args;

        initInvocationChain();
    }

    /**
     *
     */
    private void initInvocationChain() {

        Deque<Filter> filtersDeque = new LinkedList<>();

        for (Filter filter : filters) {
            filtersDeque.addFirst(filter);
            filter.setContext(this);
        }

        Filter curr = filtersDeque.pollFirst();
        curr.setNext(new DirectInvocationFilter(this.targetObjectInvocation()));

        while (true) {
            Filter next = filtersDeque.pollFirst();
            if (next == null) {
                break;
            } else {
                next.setNext(curr);
                curr = next;
            }
            break;
        }

        this.firstFilter = curr;
    }


    /**
     *
     * @return
     */
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
