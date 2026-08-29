package io.github.shield;


import io.github.shield.internal.AdaptiveThrottlingInterceptor;
import io.github.shield.internal.Validations;


/**
 * A throttler whose concurrency limit adapts to observed call outcomes (AIMD): it shrinks the
 * limit when admitted calls fail and grows it back when they succeed, staying within
 * {@code [minRequests, requests]}.
 */
public interface AdaptiveThrottler extends Throttler {

  @Override
  AdaptiveThrottler requests(int max);

  @Override
  AdaptiveThrottler maxWaitMillis(long maxWait);

  /** The lowest the adaptive limit may drop to. Must be positive and not exceed {@code requests}. */
  AdaptiveThrottler minRequests(int min);

  /** Factor applied to the current limit on each failure, in the open interval (0, 1). */
  AdaptiveThrottler backoffRatio(double ratio);

  class Config implements AdaptiveThrottler {

    private int max = 10;
    private int min = 1;
    private long wait = 500;
    private double backoffRatio = 0.5;

    @Override
    public AdaptiveThrottler requests(final int val) {
      Validations.checkArgument(val > 0, "Max requests must be positive");
      this.max = val;
      return this;
    }

    @Override
    public AdaptiveThrottler maxWaitMillis(final long val) {
      Validations.checkArgument(val > 0, "wait value must be positive");
      this.wait = val;
      return this;
    }

    @Override
    public AdaptiveThrottler minRequests(final int val) {
      Validations.checkArgument(val > 0, "Min requests must be positive");
      this.min = val;
      return this;
    }

    @Override
    public AdaptiveThrottler backoffRatio(final double val) {
      Validations.checkArgument(val > 0 && val < 1, "backoffRatio must be between 0 and 1 (exclusive)");
      this.backoffRatio = val;
      return this;
    }

    @Override
    public Interceptor build() {
      Validations.checkArgument(min <= max, "Min requests must not exceed max requests");
      return new AdaptiveThrottlingInterceptor(max, min, wait, backoffRatio);
    }
  }
}