package io.github.shield.internal;

import io.github.shield.Interceptor;
import io.github.shield.ShieldedSupplier;

import java.util.List;
import java.util.function.Supplier;

public class SupplierWrapper<T> implements ShieldedSupplier<T> {

    private final List<Interceptor> interceptors;
    private final InvokerDispatcher<T> dispatcher;
    private final Supplier<T> supplier;
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
        // interceptors arrive already sorted by order; link each one to the next so the whole
        // chain is traversed (interceptor[0] -> interceptor[1] -> ... -> supplier).
        for (int i = 0; i < interceptors.size() - 1; i++) {
            interceptors.get(i).setNext(interceptors.get(i + 1));
        }
        this.firstInterceptor = interceptors.isEmpty() ? null : interceptors.get(0);
    }

    @Override
    public T get() {
        InvocationContext<T> ctx = new InvocationContext<>(firstInterceptor, supplier);
        setContext(ctx);
        return dispatcher.invoke(ctx);
    }

    @Override
    public void close() {
        for (Interceptor interceptor : interceptors) {
            interceptor.close();
        }
    }
}
