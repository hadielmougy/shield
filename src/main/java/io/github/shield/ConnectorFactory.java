package io.github.shield;

import io.github.shield.proxy.ProxyFactory;


/**
 *
 */
public interface ConnectorFactory {


    /**
     *
     * @param type
     * @param args
     * @param <T>
     * @return
     */
    default  <T> T as(Class<T> type, Object... args) {
        Connector connector = connector();
        T component         = ProxyFactory.proxy(type, connector, args);
        return component;
    }


    /**
     *
     * @return
     */
    Connector connector();
}
