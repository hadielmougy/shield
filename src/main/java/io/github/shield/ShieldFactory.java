package io.github.shield;

import io.github.shield.internal.ComponentRegistry;
import io.github.shield.proxy.ProxyFactory;

public interface ShieldFactory {

    default  <T> T as(Class<T> type, Object... args) {
        Connector connector = connector();
        T component         = ProxyFactory.proxy(type, connector, args);
        ComponentRegistry.INSTANCE.register(component, connector);
        return component;
    }

    Connector connector();
}
