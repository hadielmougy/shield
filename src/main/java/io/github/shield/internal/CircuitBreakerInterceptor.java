package io.github.shield.internal;


import io.github.shield.Interceptor;

public interface CircuitBreakerInterceptor extends Interceptor {
    void setState(CircuitBreakerState circuitBreakerState);
}
