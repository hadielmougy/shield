package io.github.shield;

import io.github.shield.internal.CircuitBreakerOpenException;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Exercises the count-based circuit breaker state machine through the public API:
 * closed -> open (rejects) -> closed / half-open transitions.
 *
 * <p>The breaker propagates the target's own exception on a failed call and throws
 * {@link CircuitBreakerOpenException} while open, so tests distinguish the two.
 */
public class CircuitBreakerStateMachineTest {

    /** Runs the call, returning true if it was rejected by the open breaker. */
    private static boolean rejected(Supplier<Void> comp) {
        try {
            comp.get();
            return false;
        } catch (CircuitBreakerOpenException rejectedByBreaker) {
            return true;
        } catch (RuntimeException targetFailure) {
            return false;
        }
    }

    private static Supplier<Void> alwaysFailing(AtomicInteger counter, RuntimeException ex) {
        return () -> {
            counter.incrementAndGet();
            throw ex;
        };
    }

    @Test
    public void rejectsCallsWithOpenExceptionOnceOpen() {
        final AtomicInteger counter = new AtomicInteger(0);
        // window of 2, 50% threshold, long open duration so it stays open for the test
        final Supplier<Void> comp = Shield.decorate(alwaysFailing(counter, new RuntimeException()))
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        // First two calls fill the window (both fail with the target exception) and trip the breaker.
        Assert.assertFalse(rejected(comp));
        Assert.assertFalse(rejected(comp));
        Assert.assertEquals(2, counter.get());

        // The breaker is now open: the next calls are rejected without touching the target.
        Assert.assertTrue(rejected(comp));
        Assert.assertTrue(rejected(comp));
        Assert.assertEquals(2, counter.get());
    }

    @Test
    public void theTrippingCallReturnsItsOwnOutcome() {
        final AtomicInteger counter = new AtomicInteger(0);
        final Supplier<Void> comp = Shield.decorate(alwaysFailing(counter, new IllegalStateException("boom")))
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        // First failure fills half the window; its own exception propagates.
        Assert.assertThrows(IllegalStateException.class, comp::get);

        // The second call is the one that trips the breaker; it must still surface the target's
        // own exception, not CircuitBreakerOpenException.
        IllegalStateException thrown = Assert.assertThrows(IllegalStateException.class, comp::get);
        Assert.assertEquals("boom", thrown.getMessage());
        Assert.assertEquals(2, counter.get());

        // Only the following call is rejected.
        Assert.assertThrows(CircuitBreakerOpenException.class, comp::get);
    }

    @Test
    public void staysClosedWhenFailureRateBelowThreshold() {
        final AtomicInteger counter = new AtomicInteger(0);
        // Fails only on the very first call: 1 failure out of 10 = 10% < 90% threshold.
        Supplier<Void> target = () -> {
            int n = counter.incrementAndGet();
            if (n == 1) {
                throw new RuntimeException();
            }
            return null;
        };

        final Supplier<Void> comp = Shield.decorate(target)
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(90)
                        .slidingWindowSize(10)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        // None of these should be rejected; the breaker must stay closed.
        for (int i = 0; i < 20; i++) {
            Assert.assertFalse(rejected(comp));
        }
        Assert.assertEquals(20, counter.get());
    }

    @Test
    public void reopensAfterWaitDurationElapses() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        final Supplier<Void> comp = Shield.decorate(alwaysFailing(counter, new RuntimeException()))
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        rejected(comp);
        rejected(comp);
        // Open now -> rejected.
        Assert.assertTrue(rejected(comp));

        // After the wait duration the breaker returns to closed and lets the target run again.
        Thread.sleep(1200);
        Assert.assertFalse(rejected(comp));
        Assert.assertEquals(3, counter.get());
    }

    @Test
    public void subSecondWaitDurationIsHonored() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        // 300ms open duration - previously truncated to 0 seconds by Duration.getSeconds().
        final Supplier<Void> comp = Shield.decorate(alwaysFailing(counter, new RuntimeException()))
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .waitDurationInOpenState(Duration.ofMillis(300))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        rejected(comp);
        rejected(comp);
        // Still open immediately after tripping.
        Assert.assertTrue(rejected(comp));

        // After the 300ms wait it should close and admit calls again.
        Thread.sleep(600);
        Assert.assertFalse(rejected(comp));
        Assert.assertEquals(3, counter.get());
    }

    @Test
    public void ignoredExceptionsDoNotTripTheBreaker() {
        final AtomicInteger counter = new AtomicInteger(0);
        // Target throws a RuntimeException wrapping an IllegalStateException cause.
        Supplier<Void> target = () -> {
            counter.incrementAndGet();
            throw new RuntimeException(new IllegalStateException());
        };

        final Supplier<Void> comp = Shield.decorate(target)
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .ignoreExceptions(IllegalStateException.class)
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        // Because the failures are ignored, the breaker never opens and every call reaches the target.
        for (int i = 0; i < 6; i++) {
            Assert.assertFalse(rejected(comp));
        }
        Assert.assertEquals(6, counter.get());
    }

    @Test
    public void recordedExceptionsTripTheBreaker() {
        final AtomicInteger counter = new AtomicInteger(0);
        Supplier<Void> target = () -> {
            counter.incrementAndGet();
            throw new RuntimeException(new IllegalStateException());
        };

        final Supplier<Void> comp = Shield.decorate(target)
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(2)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .recordExceptions(IllegalStateException.class)
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        rejected(comp);
        rejected(comp);
        // Two recorded failures over a window of 2 = 100% -> open.
        Assert.assertTrue(rejected(comp));
        Assert.assertEquals(2, counter.get());
    }
}