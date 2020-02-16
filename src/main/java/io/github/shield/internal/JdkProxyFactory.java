package io.github.shield.internal;

import io.github.shield.ExecutorProvider;
import io.github.shield.Filter;
import io.github.shield.ProxyFactory;
import io.github.shield.ExecutorAware;

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
    private final ExecutorProvider executorProvider;

    /**
     * JDK proxy factory.
     * @param obj
     */
    public JdkProxyFactory(final Object obj) {
        this(obj, new ExecutorConfigurator());
    }


    /**
     * JDK proxy factory.
     * @param obj
     * @param provider
     */
    public JdkProxyFactory(final Object obj, final ExecutorProvider provider) {
        this.targetObjecct = obj;
        this.executorProvider = provider;
    }


    /**
     * Create proxy for the given type.
     * @param type
     * @param filters
     * @param <T>
     * @return
     */
    public <T> T create(final Class<T> type, final List<Filter> filters) {

        for (Filter filter : filters) {
            if (filter instanceof ExecutorAware) {
                ((ExecutorAware) filter).configureExecutor(executorProvider);
            }
        }

        return (T) java.lang.reflect.Proxy.newProxyInstance(
                targetObjecct.getClass().getClassLoader(),
                new Class[]{type},
                new InvocationInterceptor(targetObjecct, filters));
    }



}
