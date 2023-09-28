package io.github.shield;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExecutorConfigurer implements ExecutorProvider {


  public static final ExecutorConfigurer INSTANCE = new ExecutorConfigurer();

  private ExecutorConfigurer() {
  }


  @Override
  public ExecutorService get(final FireAndForgetFilter fireAndForgetFilter) {
    return new BoundedExecutor(new ShieldThreadFactory());
  }

  @Override
  public ExecutorService get(final TimeoutFilter timeoutFilter) {
    return new BoundedExecutor(new ShieldThreadFactory());
  }

  @Override
  public ExecutorService get(final RateLimiterFilter rateLimiterFilter) {
    return Executors.newScheduledThreadPool(8);
  }
}
