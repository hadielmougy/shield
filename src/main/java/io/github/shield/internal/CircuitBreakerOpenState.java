package io.github.shield.internal;

import io.github.shield.CircuitBreaker;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CircuitBreakerOpenState implements CircuitBreakerState {

    private final CircuitBreaker.Config config;
    private final Duration duration;
    private final CircuitBreakerFilter breaker;
    private final WindowingPolicy windowingPolicy;

    private final ScheduledExecutorService scheduledExecutorService
            = Executors.newSingleThreadScheduledExecutor();

    public CircuitBreakerOpenState(CircuitBreaker.Config config, CircuitBreakerFilter circuitBreakerFilter, WindowingPolicy windowingPolicy) {
        this.config = config;
        this.breaker = circuitBreakerFilter;
        this.duration = config.getWaitDurationInOpenState();
        this.windowingPolicy = windowingPolicy;
        scheduledExecutorService.schedule(this::close, duration.getSeconds(), TimeUnit.SECONDS);
    }

    private void close() {
        if (config.getPermittedNumberOfCallsInHalfOpenState() > 0) {
            breaker.setState(new CircuitBreakerHalfOpenState(config, breaker, windowingPolicy));
        } else {
            breaker.setState(new CircuitBreakerClosedState(config, breaker, windowingPolicy));
        }
    }

    @Override
    public Object invoke(Supplier supplier) {
        throw new CircuitBreakerOpenException();
    }
}
