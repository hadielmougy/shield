package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.InvocationCancelledException;
import io.github.shield.InvocationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;


/**
 *
 */
public class InvocationInterceptor implements InvocationHandler {


    /**
     */
    private final List<Filter> filters;

    /**
     */
    private final Object target;

    /**
     */
    private final InvokerDispatcher dispatcher;
    /**
     */
    private Filter firstFilter;


    /**
     * @param o
     * @param l
     */
    InvocationInterceptor(final Object o, final List<Filter> l) {
        this.target  = o;
        this.filters = l;
        final TargetMethodInvoker method     = new TargetMethodInvoker();
        final FallbackMethodInvoker fallback = new FallbackMethodInvoker();
        dispatcher = new InvokerDispatcher(method, fallback);
    }


    /**
     *
     * @param p
     * @param m
     * @param a
     * @return
     */
    @Override
    public Object invoke(final Object p, final Method m, final Object[] a) {
        reduceFilters(this.targetObjectInvocation(m, a));
        InvocationContext ctx = new InvocationContext(firstFilter, target, m, a);
        setContext(ctx);
        return dispatcher.invoke(ctx);
    }


    public void setContext(final InvocationContext ctx) {
        for (Filter filter : filters) {
            filter.setContext(ctx);
        }
    }


    /**
     *
     */
    private void reduceFilters(Supplier supplier) {

        Deque<Filter> filtersDeque = new LinkedList<>();

        for (Filter filter : filters) {
            filtersDeque.addFirst(filter);
        }

        Filter curr = filtersDeque.pollFirst();
        curr.setNext(new DirectInvocationFilter(supplier));

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




    /**
     *
     * @return
     * @param m
     * @param a
     */
    private Supplier targetObjectInvocation(Method m, Object[] a) {
        return () -> {
            try {
                return m.invoke(target, a);
            } catch (InvocationCancelledException th) {
                throw th;
            } catch (Throwable th) {
                throw new InvocationException(th);
            }
        };
    }




    private static class DirectInvocationFilter extends AbstractBaseFilter {


        private final Supplier valueSupplier;

        public DirectInvocationFilter(Supplier supplier) {
            this.valueSupplier = supplier;
        }


        @Override
        public boolean beforeInvocation() {
            return true;
        }

        @Override
        public void afterInvocation() {
            // do nothting
        }

        @Override
        public Object invoke() {
            return valueSupplier.get();
        }
    }

}
