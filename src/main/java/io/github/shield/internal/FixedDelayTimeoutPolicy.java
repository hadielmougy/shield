package io.github.shield.internal;

import java.util.concurrent.TimeUnit;

public class FixedDelayTimeoutPolicy extends TimeoutPolicy {
    public FixedDelayTimeoutPolicy(long delay, TimeUnit timeunit) {
        super(delay, timeunit);
    }

    /**
     *
     * @throws InterruptedException
     */
    @Override
    public void sleep() throws InterruptedException {
        timeunit.sleep(delay);
    }
}
