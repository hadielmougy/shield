package io.github.shield;


import io.github.shield.internal.ThrottlingFilter;
import io.github.shield.internal.Validations;


/**
 *
 */
public interface Throttler extends FilterFactory {

  /**
   * Set the maximum number of concurrent requests.
   *
   * @param max positive number of requests default (10)
   * @return Throttle config builder
   */
  Throttler requests(int max);


  /**
   * Maximum milliseconds to wait before give up the current thread to continue if suspended due to
   * maximum requests are already running.
   *
   * @param maxWait wait milliseconds
   * @return Throttle config builder
   */
  Throttler maxWaitMillis(long maxWait);


  /**
   *
   */
  class Config implements Throttler {

    /**
     * default maximum requests.
     */
    private int max = 10;
    /**
     * default wait millis.
     */
    private long wait = 500;

    /**
     * {@inheritDoc}
     */
    @Override
    public Throttler requests(final int val) {
      Validations.checkArgument(max > 0, "Max requests must be positive");
      this.max = val;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Throttler maxWaitMillis(final long val) {
      Validations.checkArgument(wait > 0, "wait value must be positive");
      this.wait = val;
      return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Filter build() {
      return new ThrottlingFilter(max, wait);
    }
  }
}
