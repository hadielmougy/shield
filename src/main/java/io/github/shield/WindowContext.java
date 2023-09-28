package io.github.shield;

import java.util.concurrent.atomic.AtomicInteger;

public final class WindowContext {
    private final AtomicInteger count;
    private final AtomicInteger failureCount;
    private final long startTimestamp;

    WindowContext() {
        startTimestamp = System.currentTimeMillis();
        count = new AtomicInteger(0);
        failureCount = new AtomicInteger(0);
    }

    public void increaseCount() {
        count.incrementAndGet();
    }

    public void increaseFailure() {
        failureCount.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public long getWindowSizeMillis() {
        return System.currentTimeMillis() - startTimestamp;
    }

    public long getWindowSizeSeconds() {
        return (System.currentTimeMillis() - startTimestamp) / 1000;
    }

}
