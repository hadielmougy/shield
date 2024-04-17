package io.github.shield.internal;


import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractLimiterBase extends AbstractBaseInterceptor {

  /**
   *
   */
  private final Semaphore semaphore;


  /**
   *
   */
  private final int permits;


  /**
   *
   */
  private long invokeTimeout;


  /**
   * @param max
   * @param maxWaitMillis
   */
  public AbstractLimiterBase(final int max, final long maxWaitMillis) {
    this.permits = max;
    this.semaphore = new Semaphore(max, true);
    this.invokeTimeout = maxWaitMillis;
  }


  /**
   * @return
   */
  @Override
  public boolean beforeInvocation() {
    boolean permitted = false;
    try {
      permitted = semaphore.tryAcquire(
          invokeTimeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return permitted;
  }


  @Override
  public Object invoke(Supplier supplier) {
    return invokeNext(supplier);
  }


  public void release() {
    semaphore.release();
  }


  public void releaseAll() {
    semaphore.release(permits);
  }
}
