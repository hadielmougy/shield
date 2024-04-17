package io.github.shield;


import io.github.shield.internal.ThrottlingInterceptor;
import io.github.shield.internal.Validations;


public interface Throttler extends InterceptorBuilder {

  Throttler requests(int max);

  Throttler maxWaitMillis(long maxWait);

  class Config implements Throttler {

    private int max = 10;
    private long wait = 500;

    @Override
    public Throttler requests(final int val) {
      Validations.checkArgument(max > 0, "Max requests must be positive");
      this.max = val;
      return this;
    }

    @Override
    public Throttler maxWaitMillis(final long val) {
      Validations.checkArgument(wait > 0, "wait value must be positive");
      this.wait = val;
      return this;
    }

    @Override
    public Interceptor build() {
      return new ThrottlingInterceptor(max, wait);
    }
  }
}
