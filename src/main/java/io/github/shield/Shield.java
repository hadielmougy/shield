package io.github.shield;


import io.github.shield.internal.JdkProxyFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class Shield {

    /**
     */
    private final List<Filter> filters;


    /**
     */
    private final ProxyFactory proxyFactory;



    private Shield(final Object obj) {
        this.proxyFactory = new JdkProxyFactory(Objects.requireNonNull(obj));
        this.filters = new LinkedList<>();
    }



    /**
     * @param filter
     * @return current shield object
     */
    public Shield filter(final Filter filter) {
        this.filters.add(Objects.requireNonNull(filter,
                "filter can't be null"
        ));
        return this;
    }


    /**
     * Creates new shield object that wraps the target object.
     * @param targetObject
     * @return new instance of shield
     */
    public static Shield forObject(final Object targetObject) {
        return new Shield(targetObject);
    }


    /**
     * Create proxy of the given type around the target object.
     * @param type interface type of target component
     * @param <T> interface type
     * @return proxy of the type that is passed as a parameter to this method
     */
    public  <T> T as(final Class<T> type) {

        if (filters.isEmpty()) {
            throw new IllegalStateException(
                    "At least one filter must be provided"
            );
        }

        sort(filters);

        return proxyFactory.create(type, filters);
    }


    private void sort(final List<Filter> list) {
        Collections.sort(list);
    }

}
