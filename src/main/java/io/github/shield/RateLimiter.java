package io.github.shield;

import io.github.shield.internal.RateLimiterInterceptor;
import io.github.shield.internal.Validations;


/**
 *
 */
public interface RateLimiter extends InterceptorBuilder {

  /**
   * Default rate value per second.
   */
  int DEFUALT_RATE = 10;

  /**
   * Set rate per second value.
   *
   * @param rate positive value for rate
   * @return rate limiter config builder
   */
  RateLimiter rate(int rate);


  /**
   *
   */
  class Config implements RateLimiter {

    /**
     * @see RateLimiter
     */
    private int rate = DEFUALT_RATE;

    /**
     * {@inheritDoc}
     */
    @Override
    public RateLimiter rate(final int r) {
      Validations.checkArgument(rate > 0, "rate must be positive");
      this.rate = r;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Interceptor build() {
      return new RateLimiterInterceptor(rate);
    }
  }
}
