package io.github.shield.internal;

public class TimeBasedWindowingPolicy implements WindowingPolicy {

    private final int windowSize;
    private final int targetFailureRateCeiling;

    TimeBasedWindowingPolicy(int windowSizeSeconds, int targetFailureRateCeiling) {
        this.windowSize = windowSizeSeconds;
        this.targetFailureRateCeiling = targetFailureRateCeiling;
    }
    @Override
    public boolean isDue(WindowContext state) {
        final long secondsSoFar = state.getWindowSizeSeconds();
        return secondsSoFar >= windowSize
                &&
                shouldOpen(state);
    }

    private boolean shouldOpen(WindowContext state) {
        if (state.getFailureCount() == 0) {
            return false;
        }
        float rate = (state.getFailureCount() * 100F) / state.getCount();
        return rate >= targetFailureRateCeiling;
    }
}
