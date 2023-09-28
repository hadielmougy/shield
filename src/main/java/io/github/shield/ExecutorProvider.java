package io.github.shield;

import java.util.concurrent.ExecutorService;

public interface ExecutorProvider {

    ExecutorService get(FireAndForgetFilter fireAndForgetFilter);

    ExecutorService get(TimeoutFilter timeoutFilter);

    ExecutorService get(RateLimiterFilter rateLimiterFilter);
}
