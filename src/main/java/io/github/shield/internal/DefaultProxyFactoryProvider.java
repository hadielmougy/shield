package io.github.shield.internal;

import io.github.shield.ProxyFactory;
import io.github.shield.ProxyFactoryProvider;

import java.util.Objects;

public class DefaultProxyFactoryProvider implements ProxyFactoryProvider {

    @Override
    public ProxyFactory forObject(Object obj) {
        return new JdkProxyFactory(Objects.requireNonNull(obj));
    }
}
