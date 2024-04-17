package io.github.shield.internal;

import io.github.shield.ExecutorProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExecutorConfigurer implements ExecutorProvider {


  public static final ExecutorConfigurer INSTANCE = new ExecutorConfigurer();

  private ExecutorConfigurer() {
  }


  @Override
  public ExecutorService get(final FireAndForgetInterceptor fireAndForgetFilter) {
    return new BoundedExecutor(new ShieldThreadFactory());
  }

  @Override
  public ExecutorService get(final TimeoutInterceptor timeoutFilter) {
    return new BoundedExecutor(new ShieldThreadFactory());
  }

  @Override
  public ExecutorService get(final RateLimiterInterceptor rateLimiterFilter) {
    return Executors.newScheduledThreadPool(8);
  }
}
