package io.github.shield;

public interface WindowingPolicy {
    boolean isDue(WindowContext state);
}
