package io.github.shield;


/**
 *
 */
public class RateLimiter implements FilterFactory {

  /**
   * Default rate value per second.
   */
  int DEFUALT_RATE = 10;

  private int rate = DEFUALT_RATE;

  /**
   * Set rate per second value.
   *
   * @param rate positive value for rate
   * @return rate limiter config builder
   */
  RateLimiter rate(int rate) {
    Validations.checkArgument(rate > 0, "rate must be positive");
    this.rate = rate;
    return this;
  }

  public Filter build() {
    return new RateLimiterFilter(rate);
  }
}
