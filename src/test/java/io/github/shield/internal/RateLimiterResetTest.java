package io.github.shield.internal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Verifies that resetting a limiter's window refills permits back to the maximum rather than adding
 * a fresh batch every window (which previously let the available permits grow without bound and
 * defeated the rate limit). Driven through {@link ThrottlingInterceptor} with a zero wait so the
 * assertions are deterministic and do not depend on the scheduler.
 */
public class RateLimiterResetTest {

    @Test
    public void resetRefillsToMaxAndDoesNotAccumulatePermits() {
        // max = 2 permits, 0ms acquire wait so beforeInvocation() returns immediately
        ThrottlingInterceptor limiter = new ThrottlingInterceptor(2, 0);

        // Consume both permits.
        Assert.assertTrue(limiter.beforeInvocation());
        Assert.assertTrue(limiter.beforeInvocation());
        // Window exhausted.
        Assert.assertFalse(limiter.beforeInvocation());

        // Simulate several window resets. The buggy implementation added 2 permits each time,
        // leaving 6 available; the fixed implementation caps at the configured maximum of 2.
        limiter.releaseAll();
        limiter.releaseAll();
        limiter.releaseAll();

        Assert.assertTrue(limiter.beforeInvocation());
        Assert.assertTrue(limiter.beforeInvocation());
        // Only 2 permits should be available after any number of resets.
        Assert.assertFalse(limiter.beforeInvocation());
    }
}
