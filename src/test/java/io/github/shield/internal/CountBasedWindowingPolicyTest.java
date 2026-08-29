package io.github.shield.internal;

import org.junit.Assert;
import org.junit.Test;

public class CountBasedWindowingPolicyTest {

    private static WindowContext window(int calls, int failures) {
        WindowContext ctx = new WindowContext();
        for (int i = 0; i < calls; i++) {
            ctx.increaseCount();
        }
        for (int i = 0; i < failures; i++) {
            ctx.increaseFailure();
        }
        return ctx;
    }

    @Test
    public void notDueBeforeWindowIsFull() {
        // window size 4, only 3 calls recorded so far -> not evaluated yet
        CountBasedWindowingPolicy policy = new CountBasedWindowingPolicy(4, 50);
        Assert.assertFalse(policy.isDue(window(3, 3)));
    }

    @Test
    public void notDueWhenThereAreNoFailures() {
        CountBasedWindowingPolicy policy = new CountBasedWindowingPolicy(4, 50);
        Assert.assertFalse(policy.isDue(window(4, 0)));
    }

    @Test
    public void notDueWhenFailureRateBelowCeiling() {
        // 1 failure out of 4 = 25% < 50%
        CountBasedWindowingPolicy policy = new CountBasedWindowingPolicy(4, 50);
        Assert.assertFalse(policy.isDue(window(4, 1)));
    }

    @Test
    public void dueWhenFailureRateEqualsCeiling() {
        // 2 failures out of 4 = 50% >= 50%
        CountBasedWindowingPolicy policy = new CountBasedWindowingPolicy(4, 50);
        Assert.assertTrue(policy.isDue(window(4, 2)));
    }

    @Test
    public void dueWhenFailureRateAboveCeiling() {
        // 3 failures out of 4 = 75% >= 50%
        CountBasedWindowingPolicy policy = new CountBasedWindowingPolicy(4, 50);
        Assert.assertTrue(policy.isDue(window(4, 3)));
    }

    @Test
    public void dueWhenAllCallsFail() {
        CountBasedWindowingPolicy policy = new CountBasedWindowingPolicy(2, 100);
        Assert.assertTrue(policy.isDue(window(2, 2)));
    }

    @Test
    public void notDueWhenCeilingIsHundredAndOneCallSucceeds() {
        // 1 failure out of 2 = 50% < 100%
        CountBasedWindowingPolicy policy = new CountBasedWindowingPolicy(2, 100);
        Assert.assertFalse(policy.isDue(window(2, 1)));
    }
}
