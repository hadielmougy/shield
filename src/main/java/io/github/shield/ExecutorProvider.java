package io.github.shield;

import io.github.shield.internal.FireAndForgetFilter;
import io.github.shield.internal.RateLimiterFilter;
import io.github.shield.internal.TimeoutFilter;

import java.util.concurrent.ExecutorService;

public interface ExecutorProvider {
    ExecutorService get(FireAndForgetFilter fireAndForgetFilter);
    ExecutorService get(TimeoutFilter timeoutFilter);
    ExecutorService get(RateLimiterFilter rateLimiterFilter);
}
