package io.github.shield;

import java.util.concurrent.TimeUnit;

public class Timeout implements FilterFactory {

  private long wait;
  private TimeUnit timeunit;

  public Timeout waitMillis(final long val) {
    this.wait = val;
    this.timeunit = TimeUnit.MILLISECONDS;
    return this;
  }

  public Timeout waitSeconds(final long val) {
    this.wait = val;
    this.timeunit = TimeUnit.SECONDS;
    return this;
  }

  @Override
  public Filter build() {
    return new TimeoutFilter(wait, timeunit);
  }
}
