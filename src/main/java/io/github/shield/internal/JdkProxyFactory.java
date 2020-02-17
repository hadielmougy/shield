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
     * JDK proxy factory.
     * @param obj
     */
    public JdkProxyFactory(final Object obj) {
        this.targetObjecct = obj;
    }




    /**
     * Create proxy for the given type.
     * @param type
     * @param filters
     * @param <T>
     * @return proxy
     */
    public <T> T create(final Class<T> type, final List<Filter> filters) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                targetObjecct.getClass().getClassLoader(),
                new Class[]{type},
                new InvocationInterceptor(targetObjecct, filters));
    }



}
