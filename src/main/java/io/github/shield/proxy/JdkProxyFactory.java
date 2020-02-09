package io.github.shield.proxy;

import io.github.shield.Connector;

public class JdkProxyFactory {


    public static <T> T proxy(Class<T> type, Connector connector) {
        Object target = connector.getTargetComponent();
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{type},
                new InvocationInterceptor(connector));
    }

}
