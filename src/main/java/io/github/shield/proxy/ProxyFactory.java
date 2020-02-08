package io.github.shield.proxy;

import io.github.shield.Connector;
import io.github.shield.internal.InvocationInterceptor;
import io.github.shield.util.ClassUtil;
import net.sf.cglib.proxy.Enhancer;

public class ProxyFactory {


    public static <T> T proxy(Class<T> type, Connector connector, Object[] args) {
        return  (T) baseEnhancer(type, connector).create(ClassUtil.toClassArray(args), args);
    }

    private static <T> Enhancer baseEnhancer(Class<T> type, Connector connector) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(new InvocationInterceptor(connector));
        return enhancer;
    }

}
