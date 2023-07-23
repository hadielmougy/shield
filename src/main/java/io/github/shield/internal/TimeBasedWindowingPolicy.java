package io.github.shield.internal;

public class TimeBasedWindowingPolicy implements WindowingPolicy {

    private final int windowSize;
    private final int targetFailureRateCeiling;
    private final int minimumNumberOfCalls;

    TimeBasedWindowingPolicy(int windowSizeSeconds, int targetFailureRateCeiling, int minimumNumberOfCalls) {
        this.windowSize = windowSizeSeconds;
        this.targetFailureRateCeiling = targetFailureRateCeiling;
        this.minimumNumberOfCalls = minimumNumberOfCalls;
    }

    @Override
    public boolean isDue(WindowContext state) {
        final long secondsSoFar = state.getWindowSizeSeconds();
        return secondsSoFar >= windowSize
                &&
                state.getCount() >= minimumNumberOfCalls
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
