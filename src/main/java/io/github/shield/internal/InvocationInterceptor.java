package io.github.shield.internal;

import io.github.shield.Filter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;


/**
 *
 */
public class InvocationInterceptor implements InvocationHandler {


    /**
     *
     */
    private final List<Filter> filters;

    /**
     *
     */
    private final Object targetObject;


    /**
     * @param obj
     * @param l
     */
    public InvocationInterceptor(final Object obj, final List<Filter> l) {
        this.targetObject = obj;
        this.filters = l;
    }


    /**
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        InvocationContext context = new InvocationContext(filters, targetObject, method, args);
        InvokerDispatcher invokerDispatcher = new InvokerDispatcher(new TargetMethodInvoker(), new FallbackMethodInvoker());
        return invokerDispatcher.invoke(context);
    }
}
