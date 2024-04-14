package io.github.shield.internal;

import io.github.shield.Filter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class SupplierWrapper<T> implements Supplier<T> {

    private final List<Filter> filters;
    private final InvokerDispatcher<T> dispatcher;
    private final Supplier<T> supplier;
    /**
     *
     */
    private Filter firstFilter;

    public SupplierWrapper(Supplier<T> supplier, final List<Filter> l) {
        this.filters = l;
        this.supplier = supplier;
        final TargetMethodInvoker<T> method = new TargetMethodInvoker<>();
        dispatcher = new InvokerDispatcher<>(method);
        reduceFilters();
    }

    private void setContext(final InvocationContext<T> ctx) {
        for (Filter filter : filters) {
            filter.setContext(ctx);
        }
    }


    private void reduceFilters() {

        Deque<Filter> filtersDeque = new LinkedList<>();

        for (Filter filter : filters) {
            filtersDeque.addFirst(filter);
        }

        Filter curr = filtersDeque.pollFirst();

        while (true) {
            Filter next = filtersDeque.pollFirst();
            if (next == null) {
                break;
            } else {
                next.setNext(curr);
                curr = next;
            }
            break;
        }

        this.firstFilter = curr;
    }

    @Override
    public T get() {
        InvocationContext<T> ctx = new InvocationContext<>(firstFilter, supplier);
        setContext(ctx);
        return dispatcher.invoke(ctx);
    }
}
