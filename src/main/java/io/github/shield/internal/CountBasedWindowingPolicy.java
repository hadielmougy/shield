package io.github.shield.internal;

public class CountBasedWindowingPolicy implements WindowingPolicy {

    private final int windowSize;
    private final int targetFailureRateCeiling;

    CountBasedWindowingPolicy(int windowSize, int targetFailureRateCeiling) {
        this.windowSize = windowSize;
        this.targetFailureRateCeiling = targetFailureRateCeiling;
    }

    @Override
    public boolean isDue(WindowContext state) {
        int currentCount = state.getCount();
        return currentCount >= windowSize
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
