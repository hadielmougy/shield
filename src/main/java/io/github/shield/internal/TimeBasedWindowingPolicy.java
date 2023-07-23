package io.github.shield.internal;

public class TimeBasedWindowingPolicy implements WindowingPolicy {

    private final int windowSize;
    private final int targetFailureRateCeiling;

    TimeBasedWindowingPolicy(int windowSize, int targetFailureRateCeiling) {
        this.windowSize = windowSize;
        this.targetFailureRateCeiling = targetFailureRateCeiling;
    }
    @Override
    public boolean isDue(WindowContext state) {
        return state.getWindowSizeSeconds() > windowSize
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
