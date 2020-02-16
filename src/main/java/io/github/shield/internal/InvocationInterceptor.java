package io.github.shield.internal;

import io.github.shield.ExecutorProvider;
import io.github.shield.Filter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;


/**
 *
 */
public class InvocationInterceptor implements InvocationHandler {


    /**
     */
    private final List<Filter> filters;

    /**
     */
    private final Object target;


    /**
     */
    private final InvokerDispatcher dispatcher;
    /**
     */
    private final ExecutorProvider exe;


    /**
     * @param obj
     * @param l
     * @param p
     */
    InvocationInterceptor(final Object obj, final List<Filter> l, final ExecutorProvider p) {
        this.target = obj;
        this.filters = l;
        this.exe = p;
        final TargetMethodInvoker method = new TargetMethodInvoker();
        final FallbackMethodInvoker fallback = new FallbackMethodInvoker();
        dispatcher = new InvokerDispatcher(method, fallback);
    }


    /**
     *
     * @param p
     * @param m
     * @param a
     * @return
     */
    @Override
    public Object invoke(final Object p, final Method m, final Object[] a) {
        return dispatcher.invoke(new InvocationContext(filters, target, m, a, exe));
    }
}
