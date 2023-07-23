package io.github.shield.internal;

import io.github.shield.CircuitBreaker;

import java.util.function.Supplier;

public class CircuitBreakerClosedState implements CircuitBreakerState {

    private final CircuitBreakerFilter breaker;
    private final WindowContext windowContext;
    private final WindowingPolicy windowingPolicy;
    private final BreakerExceptionChecker breakerExceptionChecker;
    private final CircuitBreakerStateFactory stateFactory;



    public CircuitBreakerClosedState(CircuitBreakerStateFactory stateFactory, BreakerExceptionChecker breakerExceptionChecker, CircuitBreakerFilter circuitBreakerFilter, WindowingPolicy windowingPolicy) {
        this.stateFactory = stateFactory;
        this.breaker = circuitBreakerFilter;
        this.windowingPolicy = windowingPolicy;
        this.windowContext = new WindowContext();
        this.breakerExceptionChecker = breakerExceptionChecker;
    }

    @Override
    public Object invoke(Supplier supplier) {
        windowContext.increaseCount();
        if (windowingPolicy.isDue(windowContext)) {
            breaker.setState(stateFactory.newOpenState());
        }
        Object result = null;
        try {
            result = supplier.get();
        } catch (Throwable th) {
            if (breakerExceptionChecker.shouldRecord(th)) {
                windowContext.increaseFailure();
            }
        }
        return result;
    }
}
