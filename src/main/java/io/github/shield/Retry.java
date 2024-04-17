package io.github.shield;

import io.github.shield.internal.BackOffTimeoutPolicy;
import io.github.shield.internal.FixedDelayTimeoutPolicy;
import io.github.shield.internal.RetryInterceptor;
import io.github.shield.internal.Validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public interface Retry extends InterceptorBuilder {

  long DEFAULT_DELAY_VALUE = 1000;

  int DEFAULT_RETRIES = 3;

  TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;

  TimeoutPolicy DEFAULT_TIMEOUT_POLICY = new FixedDelayTimeoutPolicy(DEFAULT_DELAY_VALUE,
      DEFAULT_TIMEUNIT);

  Retry delayMillis(long delay);

  Retry delaySeconds(long delay);

  Retry maxRetries(int maxRetries);

  Retry fixed();

  Retry backOff();

  Retry onException(Class<? extends Exception> ex);

  class Config implements Retry {

    private long delay = DEFAULT_DELAY_VALUE;
    private TimeUnit timeUnit = DEFAULT_TIMEUNIT;
    private int maxRetries = DEFAULT_RETRIES;
    private List<Class<? extends Exception>> exceptions = new ArrayList<>();

    private TimeoutPolicy timeoutPolicy = DEFAULT_TIMEOUT_POLICY;

    @Override
    public Retry delayMillis(final long value) {
      final String err = "delay must be positive value";
      Validations.checkArgument(delay > 0, err);
      this.delay = value;
      this.timeUnit = TimeUnit.MILLISECONDS;
      this.timeoutPolicy.setDelay(value);
      this.timeoutPolicy.setTimeunit(this.timeUnit);
      return this;
    }

    @Override
    public Retry delaySeconds(final long value) {
      final String err = "delay must be positive value";
      Validations.checkArgument(delay > 0, err);
      this.delay = value;
      this.timeUnit = TimeUnit.SECONDS;
      this.timeoutPolicy.setDelay(value);
      this.timeoutPolicy.setTimeunit(this.timeUnit);
      return this;
    }

    @Override
    public Retry maxRetries(final int value) {
      final String err = "maxRetries must be positive value";
      Validations.checkArgument(maxRetries > 0, err);
      this.maxRetries = value;
      return this;
    }

    @Override
    public Retry fixed() {
      timeoutPolicy = new FixedDelayTimeoutPolicy(delay, timeUnit);
      return this;
    }

    @Override
    public Retry backOff() {
      timeoutPolicy = new BackOffTimeoutPolicy(delay, timeUnit);
      return this;
    }

    @Override
    public Retry onException(final Class<? extends Exception> ex) {
      final String err = "exception class must not be null";
      exceptions.add(Objects.requireNonNull(ex, err));
      return this;
    }

    @Override
    public Interceptor build() {
      return new RetryInterceptor(maxRetries, exceptions, timeoutPolicy);
    }
  }
}
