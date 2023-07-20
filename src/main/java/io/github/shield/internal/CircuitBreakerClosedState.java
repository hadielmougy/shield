package io.github.shield.internal;

import io.github.shield.CircuitBreaker;
import io.github.shield.util.ExceptionUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class CircuitBreakerClosedState implements CircuitBreakerState {

    private final CircuitBreaker.Config config;
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final CountBasedCircuitBreakerFilter breaker;

    public CircuitBreakerClosedState(CircuitBreaker.Config config, CountBasedCircuitBreakerFilter countBasedCircuitBreakerFilter) {
        this.config = config;
        this.breaker = countBasedCircuitBreakerFilter;
    }

    @Override
    public Object invoke(Supplier supplier) {
        int currentCount = count.incrementAndGet();
        int windowSize = config.getSlidingWindowSize();
        if (currentCount >= windowSize
                &&
                shouldOpen(currentCount)) {
            breaker.setState(new CircuitBreakerOpenState(config, breaker));
            throw new CircuitBreakerException();
        }
        Object result = null;
        try {
            result = supplier.get();
        } catch (Throwable th) {
            if (config.getRecordExceptions().length == 0) {
                for (Class<? extends Throwable> clazz : config.getIgnoreExceptions()) {
                    if (ExceptionUtil.isClassFoundInStackTrace(th, clazz, 2)) {
                        break;
                    }
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
        int rate = currentCount / failureCount.get();
        return rate >= config.getFailureRateThreshold();
    }
}
