package io.github.shield;


import io.github.shield.internal.ProxyFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Shield {


    private List<Filter> filters;
    private final Object targetObject;

    private Shield(Object targetObject) {
        this.targetObject = targetObject;
        this.filters = new LinkedList<>();
    }


    public Shield filter(Filter filter) {
        this.filters.add(filter);
        return this;
    }


    public static Shield forObject(Object targetObject) {
        return new Shield(targetObject);
    }


    /**
     *
     * @param type
     * @param <T>
     * @return
     */
    public  <T> T as(Class<T> type) {
        if (filters.isEmpty()) {
            throw new IllegalStateException("At least one filter must be provided");
        }
        sort(filters);
        T component = ProxyFactory.proxy(type, targetObject, filters);
        return component;
    }

    private void sort(List<Filter> filters) {
        Collections.sort(filters);
    }

}
