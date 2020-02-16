package io.github.shield;


import java.util.List;

public interface ProxyFactory {

    /**
     * Creates proxy for the given type.
     * @param type
     * @param filters
     * @param <T>
     * @return
     */
    <T> T create(Class<T> type, List<Filter> filters);
}
