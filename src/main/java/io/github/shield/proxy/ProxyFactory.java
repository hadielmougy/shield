package io.github.shield.proxy;

import io.github.shield.Filter;
import io.github.shield.internal.InvocationInterceptor;
import io.github.shield.util.ClassUtil;
import net.sf.cglib.proxy.Enhancer;

import java.util.List;

/**
 *
 */
public class ProxyFactory {


    /**
     *
     * @param type
     * @param filter
     * @param args
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> type, List<Filter> filter, Object[] args) {
        return  (T) baseEnhancer(type, filter).create(ClassUtil.toClassArray(args), args);
    }


    /**
     *
     * @param type
     * @param filter
     * @param <T>
     * @return
     */
    private static <T> Enhancer baseEnhancer(Class<T> type, List<Filter> filter) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(new InvocationInterceptor(filter));
        return enhancer;
    }

}
