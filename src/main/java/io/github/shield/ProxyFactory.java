package io.github.shield;


import java.util.List;

public interface ProxyFactory {

    /**
     * Creates proxy for the given type.
     *
     * @param type    interface type
     * @param filters list of filters in the execution chain
     * @param <T>     type
     * @return proxy instance of the given type
     */
    <T> T create(Class<T> type, List<Filter> filters);
}
