package io.github.shield;

import io.github.shield.internal.RateLimiterInterceptor;
import io.github.shield.internal.TimeoutInterceptor;

import java.util.concurrent.ExecutorService;

public interface ExecutorProvider {

  ExecutorService get(TimeoutInterceptor timeoutFilter);

  ExecutorService get(RateLimiterInterceptor rateLimiterFilter);
}
