package io.github.shield;


import io.github.shield.internal.ProxyFactory;

import java.util.Collections;
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
        sort(filters);
        T component = ProxyFactory.proxy(type, filters, args);
        return component;
    }

    private void sort(List<Filter> filters) {
        Collections.sort(filters);
    }

}
