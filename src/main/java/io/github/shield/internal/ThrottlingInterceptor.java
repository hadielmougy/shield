package io.github.shield.internal;


/**
 *
 */
public class ThrottlingInterceptor extends AbstractLimiterBase {


  /**
   * @param max
   * @param maxWaitMillis
   */
  public ThrottlingInterceptor(int max, long maxWaitMillis) {
    super(max, maxWaitMillis);
  }


  @Override
  public void afterInvocation() {
    release();
  }
}
