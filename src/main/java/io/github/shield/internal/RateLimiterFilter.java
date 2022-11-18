package io.github.shield.internal;


import static java.util.concurrent.TimeUnit.SECONDS;

import io.github.shield.ExecutorProvider;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public class RateLimiterFilter extends AbstractLimiterBase {


  /**
   * @param max
   */
  public RateLimiterFilter(final int max) {
    super(max, 250);
  }

  private void resetPermits() {
    releaseAll();
  }


  @Override
  public void afterInvocation() {
    // do nothing
  }

  @Override
  public void configureExecutor(final ExecutorProvider provider) {
    this.executorService = provider.get(this);
    ((ScheduledExecutorService) executorService).scheduleAtFixedRate(this::resetPermits, 1, 1,
        SECONDS);
  }

}
