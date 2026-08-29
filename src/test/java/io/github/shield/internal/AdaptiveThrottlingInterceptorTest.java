package io.github.shield.internal;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.Supplier;

/**
 * Deterministic tests for the AIMD adaptive throttler, driving the interceptor lifecycle directly
 * (zero acquire-wait) so the assertions do not depend on timing.
 */
public class AdaptiveThrottlingInterceptorTest {

    private static AdaptiveThrottlingInterceptor throttler(int max, int min, double backoff) {
        return new AdaptiveThrottlingInterceptor(max, min, 0, backoff);
    }

    private static void runSuccess(AdaptiveThrottlingInterceptor t) {
        Assert.assertTrue(t.beforeInvocation());
        try {
            t.invoke((Supplier<String>) () -> "ok");
        } finally {
            t.afterInvocation();
        }
    }

    private static void runFailure(AdaptiveThrottlingInterceptor t) {
        Assert.assertTrue(t.beforeInvocation());
        try {
            t.invoke((Supplier<String>) () -> {
                throw new RuntimeException("boom");
            });
            Assert.fail("expected the target exception to propagate");
        } catch (RuntimeException expected) {
            Assert.assertEquals("boom", expected.getMessage());
        } finally {
            t.afterInvocation();
        }
    }

    @Test
    public void startsAtMaxAndStaysThereWhileCallsSucceed() {
        AdaptiveThrottlingInterceptor t = throttler(8, 1, 0.5);
        Assert.assertEquals(8, t.currentLimit());
        for (int i = 0; i < 5; i++) {
            runSuccess(t);
        }
        Assert.assertEquals(8, t.currentLimit());
        Assert.assertEquals(8, t.availablePermits());
    }

    @Test
    public void shrinksMultiplicativelyOnFailure() {
        AdaptiveThrottlingInterceptor t = throttler(8, 1, 0.5);

        runFailure(t);
        Assert.assertEquals(4, t.currentLimit());

        runFailure(t);
        Assert.assertEquals(2, t.currentLimit());

        runFailure(t);
        Assert.assertEquals(1, t.currentLimit());
    }

    @Test
    public void neverShrinksBelowTheConfiguredMinimum() {
        AdaptiveThrottlingInterceptor t = throttler(8, 2, 0.5);

        runFailure(t); // 8 -> 4
        runFailure(t); // 4 -> 2
        runFailure(t); // floor(2*0.5)=1 but min is 2
        Assert.assertEquals(2, t.currentLimit());
    }

    @Test
    public void growsAdditivelyOnSuccessUpToMax() {
        AdaptiveThrottlingInterceptor t = throttler(5, 1, 0.5);

        runFailure(t); // 5 -> 2
        runFailure(t); // 2 -> 1
        Assert.assertEquals(1, t.currentLimit());

        runSuccess(t); // 1 -> 2
        Assert.assertEquals(2, t.currentLimit());
        runSuccess(t); // 2 -> 3
        Assert.assertEquals(3, t.currentLimit());

        for (int i = 0; i < 10; i++) {
            runSuccess(t);
        }
        // capped at the configured maximum
        Assert.assertEquals(5, t.currentLimit());
    }

    @Test
    public void rejectsCallsThatExceedTheCurrentLimit() {
        AdaptiveThrottlingInterceptor t = throttler(4, 1, 0.5);

        runFailure(t); // limit 4 -> 2, permits back to 2 after release
        Assert.assertEquals(2, t.currentLimit());
        Assert.assertEquals(2, t.availablePermits());

        // Hold both permits without releasing.
        Assert.assertTrue(t.beforeInvocation());
        Assert.assertTrue(t.beforeInvocation());
        // Third concurrent admission is rejected (zero wait).
        Assert.assertFalse(t.beforeInvocation());

        // Release the two in-flight permits.
        t.afterInvocation();
        t.afterInvocation();
        Assert.assertTrue(t.beforeInvocation());
        t.afterInvocation();
    }
}