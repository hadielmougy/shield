package io.github.shield;

public class CircuitBreakerStateFactory {

    private final CircuitBreaker config;
    private final CircuitBreakerFilter filter;
    private final WindowingPolicy windowingPolicy;
    private final BreakerExceptionChecker breakerExceptionChecker;

    CircuitBreakerStateFactory(CircuitBreaker config, CircuitBreakerFilter filter, WindowingPolicy windowingPolicy) {
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
