package io.github.shield.internal;

import java.util.function.Supplier;

public class CircuitBreakerClosedState implements CircuitBreakerState {

    private final CircuitBreakerFilter breaker;
    private final WindowContext windowContext;
    private final WindowingPolicy windowingPolicy;
    private final BreakerExceptionChecker breakerExceptionChecker;
    private final CircuitBreakerStateFactory stateFactory;

    public CircuitBreakerClosedState(CircuitBreakerStateFactory stateFactory,
                                     BreakerExceptionChecker breakerExceptionChecker,
                                     CircuitBreakerFilter circuitBreakerFilter,
                                     WindowingPolicy windowingPolicy) {
        this.stateFactory = stateFactory;
        this.breaker = circuitBreakerFilter;
        this.windowingPolicy = windowingPolicy;
        this.breakerExceptionChecker = breakerExceptionChecker;
        this.windowContext = new WindowContext();
    }

    @Override
    public Object invoke(Supplier<?> supplier) {
        windowContext.increaseCount();
        if (windowingPolicy.isDue(windowContext)) {
            breaker.setState(stateFactory.newOpenState());
        }
        try {
            return supplier.get();
        } catch (Throwable th) {
            if (breakerExceptionChecker.shouldRecord(th)) {
                windowContext.increaseFailure();
            }
        }
        return null;
    }
}
