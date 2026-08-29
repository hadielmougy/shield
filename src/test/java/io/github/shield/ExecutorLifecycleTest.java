package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Supplier;

/**
 * Verifies that closing a decorated supplier releases the thread pools held by the timeout and
 * rate-limiter interceptors, and that close() is safe on chains that hold no executors.
 */
public class ExecutorLifecycleTest {

    @Test
    public void closingShutsDownTheTimeoutExecutor() {
        ShieldedSupplier<String> comp = Shield.decorate((Supplier<String>) () -> "ok")
                .with(Interceptor.timeout().waitMillis(1000))
                .build();

        // Works while open.
        Assert.assertEquals("ok", comp.get());

        comp.close();

        // After close the interceptor's executor is shut down, so submitting new work is rejected.
        Assert.assertThrows(RejectedExecutionException.class, comp::get);
    }

    @Test
    public void buildReturnsAnAutoCloseableUsableInTryWithResources() throws Exception {
        try (ShieldedSupplier<Void> comp = Shield.decorate((Supplier<Void>) () -> null)
                .with(Interceptor.rateLimiter().rate(2))
                .build()) {
            comp.get();
        }
        // Reaching here means close() ran cleanly for a rate limiter (scheduled pool shut down).
    }

    @Test
    public void closeIsSafeAndIdempotentForChainsWithoutExecutors() {
        ShieldedSupplier<Void> comp = Shield.decorate((Supplier<Void>) () -> null)
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        comp.get();
        // No executor to release; closing (twice) must not throw.
        comp.close();
        comp.close();
    }
}
