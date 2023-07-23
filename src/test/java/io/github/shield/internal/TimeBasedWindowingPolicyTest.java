package io.github.shield.internal;

import org.junit.Assert;
import org.junit.Test;

public class TimeBasedWindowingPolicyTest {

    @Test
    public void testIsDue() throws InterruptedException {
        TimeBasedWindowingPolicy policy = new TimeBasedWindowingPolicy(1, 50);
        WindowContext ctx = new WindowContext();
        ctx.increaseCount();
        ctx.increaseCount();
        ctx.increaseCount();
        ctx.increaseCount();
        ctx.increaseFailure();
        ctx.increaseFailure();
        Thread.sleep(1000);
        Assert.assertEquals(1, ctx.getWindowSizeSeconds());
        Assert.assertTrue(policy.isDue(ctx));
    }
}
