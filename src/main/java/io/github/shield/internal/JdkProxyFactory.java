package io.github.shield.internal;

import io.github.shield.ExecutorProvider;
import io.github.shield.Filter;
import io.github.shield.ProxyFactory;

import java.util.List;

/**
 *
 */
public final class JdkProxyFactory implements ProxyFactory {

    /**
     */
    private final Object targetObjecct;
    /**
     *
     */
    private final ExecutorProvider exe;

    /**
     * JDK proxy factory.
     * @param obj
     */
    public JdkProxyFactory(final Object obj) {
        this(obj, new ExecutorConfigurer());
    }


    /**
     * JDK proxy factory.
     * @param obj
     * @param e
     */
    public JdkProxyFactory(final Object obj, final ExecutorProvider e) {
        this.targetObjecct = obj;
        this.exe = e;
    }


    /**
     * Create proxy for the given type.
     * @param type
     * @param filters
     * @param <T>
     * @return
     */
    public <T> T create(final Class<T> type, final List<Filter> filters) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                targetObjecct.getClass().getClassLoader(),
                new Class[]{type},
                new InvocationInterceptor(targetObjecct, filters, exe));
    }



}
