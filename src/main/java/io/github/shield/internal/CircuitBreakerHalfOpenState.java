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
    private final CircuitBreakerFilter breaker;
    private final AtomicInteger numberOfAllowedRequests;
    private final Lock lock = new ReentrantLock(true);
    private final WindowingPolicy windowingPolicy;
    private final BreakerExceptionHandler breakerExceptionHandler;
    private final WindowContext windowContext;

    public CircuitBreakerHalfOpenState(CircuitBreaker.Config config, CircuitBreakerFilter circuitBreakerFilter, WindowingPolicy windowingPolicy) {
        this.config = config;
        this.breaker = circuitBreakerFilter;
        this.windowingPolicy = windowingPolicy;
        this.numberOfAllowedRequests = new AtomicInteger(config.getPermittedNumberOfCallsInHalfOpenState());
        this.breakerExceptionHandler = new BreakerExceptionHandler(config.getIgnoreExceptions(), config.getRecordExceptions());
        this.windowContext = new WindowContext();
        close();
    }

    private void close() {
        breaker.setState(new CircuitBreakerClosedState(config, breaker, windowingPolicy));
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
            if (breakerExceptionHandler.shouldRecord(th)) {
                windowContext.increaseFailure();
            }
        }
        if (remainder == 0 && windowContext.getFailureCount() == 0) {
            breaker.setState(new CircuitBreakerClosedState(config, breaker, windowingPolicy));
        }

        if (remainder == 0 && windowContext.getFailureCount() > 0) {
            breaker.setState(new CircuitBreakerOpenState(config, breaker, windowingPolicy));
        }
        return result;
    }
}
