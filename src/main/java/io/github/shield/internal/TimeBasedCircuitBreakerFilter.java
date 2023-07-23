package io.github.shield.internal;

import io.github.shield.CircuitBreaker;
import java.util.Objects;
import java.util.function.Supplier;

public class TimeBasedCircuitBreakerFilter extends AbstractBaseFilter implements CircuitBreakerFilter {

    private CircuitBreakerState state;

    public TimeBasedCircuitBreakerFilter(CircuitBreaker.Config config) {
        setState(new CircuitBreakerClosedState(config, this, new TimeBasedWindowingPolicy(config.getSlidingWindowSize(), config.getFailureRateThreshold())));
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
