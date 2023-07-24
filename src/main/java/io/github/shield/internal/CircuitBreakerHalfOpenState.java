package io.github.shield.internal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class CircuitBreakerHalfOpenState implements CircuitBreakerState {

    private final CircuitBreakerFilter breaker;
    private final AtomicInteger numberOfAllowedRequests;
    private final Lock lock = new ReentrantLock(true);
    private final BreakerExceptionChecker breakerExceptionChecker;
    private final WindowContext windowContext;
    private final CircuitBreakerStateFactory stateFactory;

    public CircuitBreakerHalfOpenState(CircuitBreakerStateFactory stateFactory,
                                       BreakerExceptionChecker breakerExceptionChecker,
                                       CircuitBreakerFilter circuitBreakerFilter,
                                       int numberOfAllowedRequests) {
        this.stateFactory = stateFactory;
        this.breaker = circuitBreakerFilter;
        this.numberOfAllowedRequests = new AtomicInteger(numberOfAllowedRequests);
        this.breakerExceptionChecker = breakerExceptionChecker;
        this.windowContext = new WindowContext();
    }

    @Override
    public Object invoke(Supplier<?> supplier) {
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

    private Object doInvoke(Supplier<?> supplier) {
        int remainder = numberOfAllowedRequests.decrementAndGet();
        try {
            return supplier.get();
        } catch (Throwable th) {
            if (breakerExceptionChecker.shouldRecord(th)) {
                windowContext.increaseFailure();
            }
        }
        if (remainder == 0 && windowContext.getFailureCount() == 0) {
            breaker.setState(stateFactory.newClosedState());
        }

        if (remainder == 0 && windowContext.getFailureCount() > 0) {
            breaker.setState(stateFactory.newOpenState());
        }
        return null;
    }
}
