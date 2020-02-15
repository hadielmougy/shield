package io.github.shield.internal;

import io.github.shield.Filter;

import java.lang.reflect.Method;
import java.util.List;


/**
 *
 */
public class InvocationInterceptor implements java.lang.reflect.InvocationHandler {


    /**
     *
     */
    private final List<Filter> filters;
    private final Object targetObject;


    /**
     *
     * @param filters
     */
    public InvocationInterceptor(Object targetObject, List<Filter> filters) {
        this.targetObject = targetObject;
        this.filters = filters;
    }




    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        InvocationContext context = new InvocationContext(filters, targetObject, method, args);
        InvokerDispatcher invokerDispatcher =
                new InvokerDispatcher(new TargetMethodInvoker(), new FallbackMethodInvoker());

        return invokerDispatcher.invoke(context);
    }
}
