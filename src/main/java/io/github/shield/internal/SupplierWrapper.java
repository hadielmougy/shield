package io.github.shield.internal;

import io.github.shield.Interceptor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class SupplierWrapper<T> implements Supplier<T> {

    private final List<Interceptor> interceptors;
    private final InvokerDispatcher<T> dispatcher;
    private final Supplier<T> supplier;
    /**
     *
     */
    private Interceptor firstInterceptor;

    public SupplierWrapper(Supplier<T> supplier, final List<Interceptor> l) {
        this.interceptors = l;
        this.supplier = supplier;
        final TargetMethodInvoker<T> method = new TargetMethodInvoker<>();
        dispatcher = new InvokerDispatcher<>(method);
        reduceFilters();
    }

    private void setContext(final InvocationContext<T> ctx) {
        for (Interceptor interceptor : interceptors) {
            interceptor.setContext(ctx);
        }
    }


    private void reduceFilters() {

        Deque<Interceptor> filtersDeque = new LinkedList<>();

        for (Interceptor interceptor : interceptors) {
            filtersDeque.addFirst(interceptor);
        }

        Interceptor curr = filtersDeque.pollFirst();

        while (true) {
            Interceptor next = filtersDeque.pollFirst();
            if (next == null) {
                break;
            } else {
                next.setNext(curr);
                curr = next;
            }
            break;
        }

        this.firstInterceptor = curr;
    }

    @Override
    public T get() {
        InvocationContext<T> ctx = new InvocationContext<>(firstInterceptor, supplier);
        setContext(ctx);
        return dispatcher.invoke(ctx);
    }
}
