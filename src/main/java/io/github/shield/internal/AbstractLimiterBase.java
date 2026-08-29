package io.github.shield.internal;


import io.github.shield.InvocationCancelledException;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLimiterBase extends AbstractBaseInterceptor {

  private final Semaphore semaphore;
  private final int permits;
  private final long invokeTimeout;

  public AbstractLimiterBase(final int max, final long maxWaitMillis) {
    this.permits = max;
    this.semaphore = new Semaphore(max, true);
    this.invokeTimeout = maxWaitMillis;
  }

  @Override
  public boolean beforeInvocation() {
    boolean permitted = false;
    try {
      permitted = semaphore.tryAcquire(invokeTimeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InvocationCancelledException("Thread interrupted while acquiring semaphore permit");
    }
    return permitted;
  }

  public void release() {
    semaphore.release();
  }


  /**
   * Resets the available permits back to the configured maximum for a new window. Draining first
   * prevents unused permits from accumulating across windows (a plain {@code release(permits)} would
   * add permits every window and let the count grow without bound).
   */
  public void releaseAll() {
    semaphore.drainPermits();
    semaphore.release(permits);
  }
}
