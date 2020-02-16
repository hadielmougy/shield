package io.github.shield.internal;

import io.github.shield.Filter;


public abstract class AbstractBaseFilter implements Filter {

    private Filter next;
    private InvocationContext context;


    @Override
    public int compareTo(Filter o) {
        return this.getOrder().compareTo(o.getOrder());
    }


    @Override
    public Integer getOrder() {
        return 0;
    }


    @Override
    public void setNext(Filter next) {
        this.next = next;
    }


    protected final Object invokeNext() {
        return next.doInvoke();
    }


    @Override
    public void setContext(InvocationContext context) {
        this.context = context;
    }


    @Override
    public InvocationContext getContext() {
        return context;
    }

    @Override
    public Object invoke() {
        return invokeNext();
    }
}
