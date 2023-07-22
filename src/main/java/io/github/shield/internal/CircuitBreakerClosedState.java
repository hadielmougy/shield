package io.github.shield.internal;

import io.github.shield.CircuitBreaker;
import io.github.shield.util.ExceptionUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class CircuitBreakerClosedState implements CircuitBreakerState {

    private final CircuitBreaker.Config config;
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final CountBasedCircuitBreakerFilter breaker;

    public CircuitBreakerClosedState(CircuitBreaker.Config config, CountBasedCircuitBreakerFilter circuitBreakerFilter) {
        this.config = config;
        this.breaker = circuitBreakerFilter;
    }

    @Override
    public Object invoke(Supplier supplier) {
        int currentCount = count.incrementAndGet();
        int windowSize = config.getSlidingWindowSize();
        if (currentCount >= windowSize
                &&
                shouldOpen(currentCount)) {
            breaker.setState(new CircuitBreakerOpenState(config, breaker));
        }
        Object result = null;
        try {
            result = supplier.get();
        } catch (Throwable th) {
            if (config.getRecordExceptions().length == 0) {
                boolean shouldIgnore = Arrays.stream(config.getIgnoreExceptions())
                        .anyMatch(clazz -> ExceptionUtil.isClassFoundInStackTrace(th, clazz, 2));
                if (!shouldIgnore) {
                    failureCount.incrementAndGet();
                }
            } else {
                for (Class<? extends Throwable> clazz : config.getRecordExceptions()) {
                    if (ExceptionUtil.isClassFoundInStackTrace(th, clazz, 2)) {
                        failureCount.incrementAndGet();
                        break;
                    }
                }
            }
        }
        return result;
    }

    private boolean shouldOpen(int currentCount) {
        if (failureCount.get() == 0) {
            return false;
        }
        float rate = (failureCount.get() * 100F) / currentCount;
        return rate >= config.getFailureRateThreshold();
    }
}
