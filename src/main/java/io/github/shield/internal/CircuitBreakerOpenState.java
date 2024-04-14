package io.github.shield.internal;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CircuitBreakerOpenState implements CircuitBreakerState {

    private final Duration duration;
    private final CircuitBreakerFilter breaker;
    private final CircuitBreakerStateFactory stateFactory;
    private final int permittedNumberOfCallsInHalfOpenState;

    private final ScheduledExecutorService scheduledExecutorService
            = Executors.newSingleThreadScheduledExecutor();

    public CircuitBreakerOpenState(CircuitBreakerStateFactory stateFactory,
                                   CircuitBreakerFilter circuitBreakerFilter,
                                   Duration waitDurationInOpenState,
                                   int permittedNumberOfCallsInHalfOpenState) {
        this.stateFactory = stateFactory;
        this.breaker = circuitBreakerFilter;
        this.duration = waitDurationInOpenState;
        this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
        scheduledExecutorService.schedule(this::close, duration.getSeconds(), TimeUnit.SECONDS);
    }

    private void close() {
        if (permittedNumberOfCallsInHalfOpenState > 0) {
            breaker.setState(stateFactory.newHalfOpenState());
        } else {
            breaker.setState(stateFactory.newClosedState());
        }
    }

    @Override
    public Object invoke(Supplier<?> supplier) {
        throw new CircuitBreakerOpenException();
    }
}
