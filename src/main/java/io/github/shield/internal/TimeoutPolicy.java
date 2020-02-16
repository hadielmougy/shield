package io.github.shield.internal;

import java.util.concurrent.TimeUnit;

public abstract class TimeoutPolicy {

    protected final long delay;
    protected final TimeUnit timeunit;

    public TimeoutPolicy(long delay, TimeUnit timeunit) {
        this.delay = delay;
        this.timeunit = timeunit;
    }

    public abstract void sleep() throws InterruptedException;
}
