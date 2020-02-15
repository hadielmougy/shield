package io.github.shield.internal;

import io.github.shield.Filter;

import java.util.List;

/**
 *
 */
public class ProxyFactory {


    /**
     *
     * @param type
     * @param target
     * @param filters
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> type, Object target, List<Filter> filters) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{type},
                new InvocationInterceptor(target, filters));
    }



}
