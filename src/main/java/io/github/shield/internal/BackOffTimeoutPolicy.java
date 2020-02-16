package io.github.shield.internal;

import java.util.concurrent.TimeUnit;

public class BackOffTimeoutPolicy extends TimeoutPolicy {

    private long currentDelay;

    public BackOffTimeoutPolicy(final long delay, final TimeUnit timeunit) {
        super(delay, timeunit);
        currentDelay = delay;
    }

    @Override
    public void sleep() throws InterruptedException {
        timeunit.sleep(currentDelay);
        currentDelay = (long) Math.pow(currentDelay, 2);
    }
}
