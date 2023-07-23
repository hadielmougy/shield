package io.github.shield.internal;


import io.github.shield.Filter;

public interface CircuitBreakerFilter extends Filter {
    void setState(CircuitBreakerState circuitBreakerState);
}
