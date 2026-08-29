package io.github.shield.internal;

import io.github.shield.TimeoutPolicy;

import java.util.concurrent.TimeUnit;

public class BackOffTimeoutPolicy extends TimeoutPolicy {

  private long currentDelay;

  public BackOffTimeoutPolicy(final long delay, final TimeUnit timeunit) {
    super(delay, timeunit);
    currentDelay = delay;
  }

  @Override
  public void sleep() throws InterruptedException {
    timeunit.sleep(currentDelay);
    currentDelay = currentDelay * 2;
  }

  /** Visible for testing: the delay that the next {@link #sleep()} will wait. */
  long getCurrentDelay() {
    return currentDelay;
  }


  @Override
  public BackOffTimeoutPolicy clone() {
    return new BackOffTimeoutPolicy(delay, timeunit);
  }
}
