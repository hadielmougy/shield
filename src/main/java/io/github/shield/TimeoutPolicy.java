package io.github.shield;

import java.util.concurrent.TimeUnit;

public abstract class TimeoutPolicy implements Cloneable {

    protected long delay;
    protected TimeUnit timeunit;

    public TimeoutPolicy(long delay, TimeUnit timeunit) {
        this.delay = delay;
        this.timeunit = timeunit;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setTimeunit(TimeUnit timeunit) {
        this.timeunit = timeunit;
    }

    public abstract void sleep() throws InterruptedException;


    @Override
    public abstract TimeoutPolicy clone();
}
