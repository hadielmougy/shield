package io.github.shield.internal;

import io.github.shield.CircuitBreaker;

import java.util.Objects;
import java.util.function.Supplier;


public class CountBasedCircuitBreakerInterceptor extends AbstractBaseInterceptor implements CircuitBreakerInterceptor {

    private CircuitBreakerState state;

    public CountBasedCircuitBreakerInterceptor(CircuitBreaker.Config config) {
        WindowingPolicy policy = new CountBasedWindowingPolicy(config.getSlidingWindowSize(), config.getFailureRateThreshold());
        setState(new CircuitBreakerStateFactory(config, this, policy).newClosedState());
    }

    @Override
    public synchronized void setState(CircuitBreakerState state) {
        this.state = Objects.requireNonNull(state);
    }

    @Override
    public boolean beforeInvocation() {
        return true;
    }

    @Override
    public void afterInvocation() {
        // do nothing
    }

    @Override
    public Object invoke(Supplier supplier) {
        return state.invoke(()-> this.invokeNext(supplier));
    }
}
