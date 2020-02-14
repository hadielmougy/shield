package io.github.shield.internal;

import io.github.shield.Filter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;


/**
 *
 */
public class InvocationInterceptor implements MethodInterceptor {


    /**
     *
     */
    private final List<Filter> filters;


    /**
     *
     * @param filters
     */
    public InvocationInterceptor(List<Filter> filters) {
        this.filters = filters;
    }


    /**
     *
     * @param obj
     * @param method
     * @param args
     * @param proxy
     * @return
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {

        InvocationContext context = new InvocationContext(filters, obj, method, args, proxy);
        InvokerDispatcher invokerDispatcher =
                new InvokerDispatcher(new TargetMethodInvoker(), new FallbackMethodInvoker());

        return invokerDispatcher.invoke(context);

    }
}
