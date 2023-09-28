package io.github.shield;

import java.util.concurrent.TimeUnit;

public class FixedDelayTimeoutPolicy extends TimeoutPolicy {

  public FixedDelayTimeoutPolicy(long delay, TimeUnit timeunit) {
    super(delay, timeunit);
  }

  /**
   * @throws InterruptedException
   */
  @Override
  public void sleep() throws InterruptedException {
    timeunit.sleep(delay);
  }


  @Override
  public FixedDelayTimeoutPolicy clone() {
    return new FixedDelayTimeoutPolicy(delay, timeunit);
  }
}
