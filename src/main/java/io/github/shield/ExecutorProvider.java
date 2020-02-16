package io.github.shield;

import io.github.shield.internal.FireAndForgetFilter;
import io.github.shield.internal.RateLimiterFilter;
import io.github.shield.internal.TimeoutFilter;

public interface ExecutorProvider {
    void provide(FireAndForgetFilter fireAndForgetFilter);
    void provide(TimeoutFilter timeoutFilter);
    void provide(RateLimiterFilter rateLimiterFilter);
}
