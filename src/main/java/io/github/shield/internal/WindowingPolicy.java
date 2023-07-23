package io.github.shield.internal;

public interface WindowingPolicy {
    boolean isDue(WindowContext state);
}
