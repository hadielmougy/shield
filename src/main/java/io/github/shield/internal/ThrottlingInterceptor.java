package io.github.shield.internal;

public class ThrottlingInterceptor extends AbstractLimiterBase {


  public ThrottlingInterceptor(int max, long maxWaitMillis) {
    super(max, maxWaitMillis);
  }


  @Override
  public void afterInvocation() {
    release();
  }
}
