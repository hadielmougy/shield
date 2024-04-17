package io.github.shield.internal;

import io.github.shield.CircuitBreaker;

public class CircuitBreakerStateFactory {

    private final CircuitBreaker.Config config;
    private final CircuitBreakerInterceptor filter;
    private final WindowingPolicy windowingPolicy;
    private final BreakerExceptionChecker breakerExceptionChecker;

    CircuitBreakerStateFactory(CircuitBreaker.Config config, CircuitBreakerInterceptor filter, WindowingPolicy windowingPolicy) {
        this.config = config;
        this.filter = filter;
        this.windowingPolicy = windowingPolicy;
        this.breakerExceptionChecker = new BreakerExceptionChecker(config.getIgnoreExceptions(), config.getRecordExceptions());
    }

    public CircuitBreakerState newClosedState() {
        return new CircuitBreakerClosedState(this,
                breakerExceptionChecker,
                filter,
                windowingPolicy);
    }

    public CircuitBreakerState newOpenState() {
        return new CircuitBreakerOpenState(this,
                filter,
                config.getWaitDurationInOpenState(),
                config.getPermittedNumberOfCallsInHalfOpenState());
    }

    public CircuitBreakerState newHalfOpenState() {
        return new CircuitBreakerHalfOpenState(this,
                breakerExceptionChecker,
                filter,
                config.getPermittedNumberOfCallsInHalfOpenState());
    }
}
