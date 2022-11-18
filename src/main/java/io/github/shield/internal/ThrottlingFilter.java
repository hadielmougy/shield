package io.github.shield.internal;


/**
 *
 */
public class ThrottlingFilter extends AbstractLimiterBase {


  /**
   * @param max
   * @param maxWaitMillis
   */
  public ThrottlingFilter(int max, long maxWaitMillis) {
    super(max, maxWaitMillis);
  }


  @Override
  public void afterInvocation() {
    release();
  }
}
