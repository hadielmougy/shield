package io.github.shield;


import io.github.shield.internal.ProxyFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Shield {


    private List<Filter> filters;
    private Object targetObject;


    public Shield() {
        this.filters = new LinkedList<>();

    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }


    public Shield forObject(Object targetObject) {
        this.targetObject = targetObject;
        return this;
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
