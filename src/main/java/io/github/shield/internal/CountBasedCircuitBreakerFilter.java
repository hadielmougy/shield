package io.github.shield.internal;

import io.github.shield.CircuitBreaker;
import io.github.shield.Filter;
import java.util.function.Supplier;


public class CountBasedCircuitBreakerFilter implements CircuitBreakerFilter {

    private CircuitBreakerState state;

    public CountBasedCircuitBreakerFilter(CircuitBreaker.Config config) {
        setState(new CircuitBreakerClosedState(config, this));
    }

    synchronized void setState(CircuitBreakerState state) {
        this.state = state;
    }

    @Override
    public boolean beforeInvocation() {
        return false;
    }

    @Override
    public void afterInvocation() {

    }

    @Override
    public Integer getOrder() {
        return null;
    }

    @Override
    public void setNext(Filter next) {

    }

    @Override
    public Object invoke(Supplier supplier) {
        return state.invoke(supplier);
    }

    @Override
    public void setContext(InvocationContext context) {

    }

    @Override
    public InvocationContext getContext() {
        return null;
    }

    @Override
    public int compareTo(Filter o) {
        return 0;
    }
}
