package io.github.shield.internal;

import io.github.shield.util.ExceptionUtil;

import java.util.function.Supplier;

public class CircuitBreakerClosedState implements CircuitBreakerState {

    private final CircuitBreakerInterceptor breaker;
    private final WindowContext windowContext;
    private final WindowingPolicy windowingPolicy;
    private final BreakerExceptionChecker breakerExceptionChecker;
    private final CircuitBreakerStateFactory stateFactory;

    public CircuitBreakerClosedState(CircuitBreakerStateFactory stateFactory,
                                     BreakerExceptionChecker breakerExceptionChecker,
                                     CircuitBreakerInterceptor circuitBreakerFilter,
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
        Throwable failure = null;
        Object result = null;
        try {
            result = supplier.get();
        } catch (Throwable th) {
            if (breakerExceptionChecker.shouldRecord(th)) {
                windowContext.increaseFailure();
            }
            failure = th;
        }
        // Evaluate the trip condition after the current call has been recorded, so the decision
        // includes this call's outcome. The call that trips the breaker still returns its own
        // result/exception; only subsequent calls are rejected.
        if (windowingPolicy.isDue(windowContext)) {
            breaker.setState(stateFactory.newOpenState());
        }
        if (failure != null) {
            throw ExceptionUtil.sneakyThrow(failure);
        }
        return result;
    }
}
