package io.github.shield.internal;

import io.github.shield.CircuitBreaker;

import java.util.function.Supplier;

public class CircuitBreakerHalfOpenState implements CircuitBreakerState {

    private final CircuitBreaker.Config config;
    private final CountBasedCircuitBreakerFilter breaker;

    public CircuitBreakerHalfOpenState(CircuitBreaker.Config config, CountBasedCircuitBreakerFilter countBasedCircuitBreakerFilter) {
        this.config = config;
        this.breaker = countBasedCircuitBreakerFilter;
        close();
    }

    private void close() {
        breaker.setState(new CircuitBreakerClosedState(config, breaker));
    }

    @Override
    public Object invoke(Supplier supplier) {
        throw new CircuitBreakerException();
    }
}
