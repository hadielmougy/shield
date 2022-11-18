package io.github.shield;

import io.github.shield.internal.TimeoutFilter;
import java.util.concurrent.TimeUnit;

public interface Timeout extends FilterFactory {


  Timeout waitMillis(long val);

  Timeout waitSeconds(long val);

  class Config implements Timeout {

    private long wait;
    private TimeUnit timeunit;

    @Override
    public Timeout waitMillis(final long val) {
      this.wait = val;
      this.timeunit = TimeUnit.MILLISECONDS;
      return this;
    }

    @Override
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
}
