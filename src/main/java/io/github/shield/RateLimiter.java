package io.github.shield;

import io.github.shield.internal.RateLimiterInterceptor;
import io.github.shield.internal.Validations;


public interface RateLimiter extends InterceptorBuilder {

  int DEFUALT_RATE = 10;

  RateLimiter rate(int rate);

  class Config implements RateLimiter {

    private int rate = DEFUALT_RATE;

    @Override
    public RateLimiter rate(final int r) {
      Validations.checkArgument(rate > 0, "rate must be positive");
      this.rate = r;
      return this;
    }

    @Override
    public Interceptor build() {
      return new RateLimiterInterceptor(rate);
    }
  }
}
