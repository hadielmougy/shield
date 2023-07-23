package io.github.shield.internal;

import io.github.shield.CircuitBreaker;
import io.github.shield.util.ExceptionUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class CircuitBreakerHalfOpenState implements CircuitBreakerState {

    private final CircuitBreaker.Config config;
    private final CountBasedCircuitBreakerFilter breaker;
    private final AtomicInteger numberOfAllowedRequests;
    private final Lock lock = new ReentrantLock(true);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    public CircuitBreakerHalfOpenState(CircuitBreaker.Config config, CountBasedCircuitBreakerFilter countBasedCircuitBreakerFilter) {
        this.config = config;
        this.breaker = countBasedCircuitBreakerFilter;
        this.numberOfAllowedRequests = new AtomicInteger(config.getPermittedNumberOfCallsInHalfOpenState());
        close();
    }

    private void close() {
        breaker.setState(new CircuitBreakerClosedState(config, breaker));
    }

    @Override
    public Object invoke(Supplier supplier) {
        boolean acquired = false;
        try {
            acquired = lock.tryLock();
            if (!acquired) {
                throw new CircuitBreakerOpenException();
            }
            return doInvoke(supplier);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    private Object doInvoke(Supplier supplier) {
        int remainder = numberOfAllowedRequests.decrementAndGet();
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
        if (remainder == 0 && failureCount.get() == 0) {
            breaker.setState(new CircuitBreakerClosedState(config, breaker));
        }

        if (remainder == 0 && failureCount.get() > 0) {
            breaker.setState(new CircuitBreakerOpenState(config, breaker));
        }
        return result;
    }
}
