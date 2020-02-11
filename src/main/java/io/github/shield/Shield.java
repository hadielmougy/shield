package io.github.shield;


import io.github.shield.internal.ProxyFactory;

import java.util.LinkedList;
import java.util.List;

public class Shield {


    private List<Filter> filters;


    public Shield() {
        this.filters = new LinkedList<>();

    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }


    /**
     *
     * @param type
     * @param args
     * @param <T>
     * @return
     */
    public  <T> T as(Class<T> type, Object... args) {
        T component = ProxyFactory.proxy(type, filters, args);
        return component;
    }

}
