package io.github.shield.internal;

import io.github.shield.InvocationCancelledException;
import io.github.shield.util.ExceptionUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * A concurrency throttler whose limit adapts to observed call outcomes using an
 * additive-increase / multiplicative-decrease (AIMD) policy:
 *
 * <ul>
 *   <li>the limit starts at {@code maxLimit} and is never allowed above it;</li>
 *   <li>each failed (or timed-out) call multiplies the limit by {@code backoffRatio}, floored at
 *       {@code minLimit}, shedding load while the downstream is struggling;</li>
 *   <li>each successful call grows the limit back by one, up to {@code maxLimit}.</li>
 * </ul>
 *
 * <p>Local rejections (a call that cannot acquire a permit within the wait budget) are the limiter
 * doing its job and do not themselves change the limit; only the outcome of admitted calls does.
 */
public class AdaptiveThrottlingInterceptor extends AbstractBaseInterceptor {

  private final int maxLimit;
  private final int minLimit;
  private final long waitMillis;
  private final double backoffRatio;

  private final ResizableSemaphore semaphore;
  private final AtomicInteger currentLimit;
  private final Object limitLock = new Object();

  public AdaptiveThrottlingInterceptor(final int maxLimit,
                                       final int minLimit,
                                       final long waitMillis,
                                       final double backoffRatio) {
    this.maxLimit = maxLimit;
    this.minLimit = minLimit;
    this.waitMillis = waitMillis;
    this.backoffRatio = backoffRatio;
    this.currentLimit = new AtomicInteger(maxLimit);
    this.semaphore = new ResizableSemaphore(maxLimit);
  }

  @Override
  public boolean beforeInvocation() {
    try {
      return semaphore.tryAcquire(waitMillis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InvocationCancelledException("Thread interrupted while acquiring throttler permit");
    }
  }

  @Override
  public Object invoke(final Supplier supplier) {
    try {
      Object result = invokeNext(supplier);
      onSuccess();
      return result;
    } catch (Throwable th) {
      onFailure();
      throw ExceptionUtil.sneakyThrow(th);
    }
  }

  @Override
  public void afterInvocation() {
    semaphore.release();
  }

  /**
   * Additive increase: grow the limit by one permit on success, never above the maximum.
   */
  private void onSuccess() {
    synchronized (limitLock) {
      int current = currentLimit.get();
      if (current < maxLimit) {
        currentLimit.set(current + 1);
        semaphore.release(1);
      }
    }
  }

  /**
   * Multiplicative decrease: shrink the limit by {@code backoffRatio} on failure, never below the
   * minimum.
   */
  private void onFailure() {
    synchronized (limitLock) {
      int current = currentLimit.get();
      int reduced = Math.max(minLimit, (int) Math.floor(current * backoffRatio));
      int delta = current - reduced;
      if (delta > 0) {
        currentLimit.set(reduced);
        semaphore.reduce(delta);
      }
    }
  }

  /** Visible for testing: the current concurrency limit. */
  int currentLimit() {
    return currentLimit.get();
  }

  /** Visible for testing: permits currently available for new calls. */
  int availablePermits() {
    return semaphore.availablePermits();
  }

  /**
   * A {@link Semaphore} that can shrink its permit pool, exposing the otherwise-protected
   * {@code reducePermits}.
   */
  private static final class ResizableSemaphore extends Semaphore {
    ResizableSemaphore(final int permits) {
      super(permits, true);
    }

    void reduce(final int n) {
      reducePermits(n);
    }
  }
}