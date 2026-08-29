package io.github.shield.internal;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class BackOffTimeoutPolicyTest {

    @Test
    public void delayDoublesEachTimeRatherThanSquaring() throws InterruptedException {
        BackOffTimeoutPolicy policy = new BackOffTimeoutPolicy(2, TimeUnit.MILLISECONDS);

        Assert.assertEquals(2, policy.getCurrentDelay());
        policy.sleep();
        Assert.assertEquals(4, policy.getCurrentDelay());
        policy.sleep();
        // doubling -> 8; the old squaring bug would produce 16 here
        Assert.assertEquals(8, policy.getCurrentDelay());
        policy.sleep();
        Assert.assertEquals(16, policy.getCurrentDelay());
    }

    @Test
    public void cloneRestartsFromTheInitialDelay() throws InterruptedException {
        BackOffTimeoutPolicy policy = new BackOffTimeoutPolicy(5, TimeUnit.MILLISECONDS);
        policy.sleep();
        policy.sleep();
        Assert.assertEquals(20, policy.getCurrentDelay());

        BackOffTimeoutPolicy fresh = policy.clone();
        Assert.assertEquals(5, fresh.getCurrentDelay());
    }
}