package io.github.shield.internal;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CircuitBreakerOpenState implements CircuitBreakerState {

    private static final ThreadFactory DAEMON_THREAD_FACTORY = runnable -> {
        Thread t = new Thread(runnable, "shield-circuit-breaker-timer");
        t.setDaemon(true);
        return t;
    };

    private final Duration duration;
    private final CircuitBreakerInterceptor breaker;
    private final CircuitBreakerStateFactory stateFactory;
    private final int permittedNumberOfCallsInHalfOpenState;

    private final ScheduledExecutorService scheduledExecutorService
            = Executors.newSingleThreadScheduledExecutor(DAEMON_THREAD_FACTORY);

    public CircuitBreakerOpenState(CircuitBreakerStateFactory stateFactory,
                                   CircuitBreakerInterceptor circuitBreakerFilter,
                                   Duration waitDurationInOpenState,
                                   int permittedNumberOfCallsInHalfOpenState) {
        this.stateFactory = stateFactory;
        this.breaker = circuitBreakerFilter;
        this.duration = waitDurationInOpenState;
        this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
        scheduledExecutorService.schedule(this::transition, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void transition() {
        try {
            close();
        } finally {
            // The scheduler fires exactly once, so shut it down to avoid leaking a thread
            // every time the circuit opens.
            scheduledExecutorService.shutdown();
        }
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