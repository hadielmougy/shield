package io.github.shield;

import java.util.Objects;
import java.util.function.Supplier;

public class TimeBasedCircuitBreakerFilter extends AbstractBaseFilter implements CircuitBreakerFilter {

    private CircuitBreakerState state;

    public TimeBasedCircuitBreakerFilter(CircuitBreaker config) {
        int windowSize      = config.getSlidingWindowSize();
        int failureRate     = config.getFailureRateThreshold();
        int numberOfCalls   = config.getMinimumNumberOfCalls();

        WindowingPolicy policy = new TimeBasedWindowingPolicy(windowSize, failureRate, numberOfCalls);
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
