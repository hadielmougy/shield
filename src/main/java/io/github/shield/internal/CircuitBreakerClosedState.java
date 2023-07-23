package io.github.shield.internal;

import io.github.shield.CircuitBreaker;
import io.github.shield.util.ExceptionUtil;

import java.util.Arrays;
import java.util.function.Supplier;

public class CircuitBreakerClosedState implements CircuitBreakerState {

    private final CircuitBreaker.Config config;
    private final CircuitBreakerFilter breaker;
    private final WindowContext windowContext;
    private final WindowingPolicy windowingPolicy;
    private final BreakerExceptionHandler breakerExceptionHandler;

    public CircuitBreakerClosedState(CircuitBreaker.Config config, CircuitBreakerFilter circuitBreakerFilter, WindowingPolicy windowingPolicy) {
        this.config = config;
        this.breaker = circuitBreakerFilter;
        this.windowingPolicy = windowingPolicy;
        this.windowContext = new WindowContext();
        this.breakerExceptionHandler = new BreakerExceptionHandler(config.getIgnoreExceptions(), config.getRecordExceptions());
    }

    @Override
    public Object invoke(Supplier supplier) {
        windowContext.increaseCount();
        if (windowingPolicy.isDue(windowContext)) {
            breaker.setState(new CircuitBreakerOpenState(config, breaker, windowingPolicy));
        }
        Object result = null;
        try {
            result = supplier.get();
        } catch (Throwable th) {
            if (breakerExceptionHandler.shouldRecord(th)) {
                windowContext.increaseFailure();
            }
        }
        return result;
    }
}
