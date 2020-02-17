package io.github.shield.internal;

import io.github.shield.Filter;

import java.util.concurrent.ExecutorService;


public abstract class AbstractBaseFilter implements Filter {

    /**
     * Next filter.
     */
    private Filter next;

    /**
     */
    protected ExecutorService executorService;



    /**
     * Context holder.
     */
    private ThreadLocal<InvocationContext> context = new ThreadLocal<>();


    public AbstractBaseFilter() {
        configureExecutor(ExecutorConfigurer.INSTANCE);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Filter o) {
        return this.getOrder().compareTo(o.getOrder());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getOrder() {
        return 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setNext(final Filter n) {
        this.next = n;
    }


    protected final Object invokeNext() {
        return next.doInvoke();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setContext(final InvocationContext ctx) {
        this.context.set(ctx);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public InvocationContext getContext() {
        return context.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke() {
        return invokeNext();
    }


    @VisibleForTest
    private void setExecutorService(final ExecutorService exe) {
        this.executorService = exe;
    }

}
