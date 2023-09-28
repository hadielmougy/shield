package io.github.shield;


public interface CircuitBreakerFilter extends Filter {
    void setState(CircuitBreakerState circuitBreakerState);
}
