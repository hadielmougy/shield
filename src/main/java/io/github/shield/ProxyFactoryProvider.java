package io.github.shield;

public interface ProxyFactoryProvider {
    ProxyFactory forObject(Object obj);
}
