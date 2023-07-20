package io.github.shield;

import io.github.shield.internal.CircuitBreakerFilter;
import io.github.shield.internal.CountBasedCircuitBreakerFilter;
import io.github.shield.internal.TimeBasedCircuitBreakerFilter;
import io.github.shield.internal.Validations;
import java.time.Duration;

public interface CircuitBreaker extends FilterFactory {

  enum WindowType { TIME_BASED, COUNT_BASED }

  CircuitBreaker failureRateThreshold(int threshold);

  CircuitBreaker waitDurationInOpenState(Duration duration);

  CircuitBreaker permittedNumberOfCallsInHalfOpenState(int numOfCalls);

  CircuitBreaker minimumNumberOfCalls(int threshold);

  CircuitBreaker slidingWindowType(WindowType windowType);

  CircuitBreaker slidingWindowSize(int size);

  CircuitBreaker recordExceptions(Class<? extends Throwable>... ex);

  CircuitBreaker ignoreExceptions(Class<? extends Throwable>... ex);

  class Config implements CircuitBreaker, Cloneable {

    private int failureRateThreshold = 50;
    private Duration waitDurationInOpenState = Duration.ofSeconds(1);
    private int permittedNumberOfCallsInHalfOpenState = 0;
    private int minimumNumberOfCalls = 100;
    private WindowType slidingWindowType = WindowType.COUNT_BASED;
    private int slidingWindowSize = 100;
    private Class<? extends Throwable>[] recordExceptions = new Class[]{};
    private Class<? extends Throwable>[] ignoreExceptions = new Class[]{};

    @Override
    public CircuitBreaker failureRateThreshold(int threshold) {
      Validations.checkArgument(threshold > 0,
              "Threshold must be greater than 0"
      );
      this.failureRateThreshold = threshold;
      return this;
    }

    @Override
    public CircuitBreaker waitDurationInOpenState(Duration duration) {
      Validations.checkArgument(duration != null,
              "Threshold must be greater than 0"
      );
      this.waitDurationInOpenState = duration;
      return this;
    }

    @Override
    public CircuitBreaker permittedNumberOfCallsInHalfOpenState(int numOfCalls) {
      Validations.checkArgument(numOfCalls > 0,
              "PermittedNumberOfCallsInHalfOpenState must be greater than 0"
      );
      this.permittedNumberOfCallsInHalfOpenState = numOfCalls;
      return this;
    }

    @Override
    public CircuitBreaker minimumNumberOfCalls(int threshold) {
      Validations.checkArgument(threshold > 0,
              "MinimumNumberOfCalls must be greater than 0"
      );
      this.minimumNumberOfCalls = threshold;
      return this;
    }

    @Override
    public CircuitBreaker slidingWindowType(WindowType windowType) {
      Validations.checkArgument(windowType != null,
              "WindowType can't be null"
      );
      this.slidingWindowType = windowType;
      return this;
    }

    @Override
    public CircuitBreaker slidingWindowSize(int size) {
      Validations.checkArgument(size > 0,
              "Size must be greater than 0"
      );
      this.slidingWindowSize = size;
      return this;
    }

    @Override
    public CircuitBreaker recordExceptions(Class<? extends Throwable>... ex) {
      Validations.checkArgument(ex != null,
              "EecordExceptions can't be null"
      );
      this.recordExceptions = ex;
      return this;
    }

    @Override
    public CircuitBreaker ignoreExceptions(Class<? extends Throwable>... ex) {
      Validations.checkArgument(ex != null,
              "IgnoreExceptions can't be null"
      );
      this.ignoreExceptions = ex;
      return this;
    }

    @Override
    public Filter build() {
      if (this.slidingWindowType == WindowType.COUNT_BASED) {
        return new CountBasedCircuitBreakerFilter(this.clone());
      } else if (this.slidingWindowType == WindowType.TIME_BASED) {
        return new TimeBasedCircuitBreakerFilter(this.clone());
      }
      throw new IllegalStateException("");
    }


    public int getFailureRateThreshold() {
      return failureRateThreshold;
    }

    public Duration getWaitDurationInOpenState() {
      return waitDurationInOpenState;
    }

    public int getPermittedNumberOfCallsInHalfOpenState() {
      return permittedNumberOfCallsInHalfOpenState;
    }

    public int getMinimumNumberOfCalls() {
      return minimumNumberOfCalls;
    }

    public int getSlidingWindowSize() {
      return slidingWindowSize;
    }

    public Class<? extends Throwable>[] getRecordExceptions() {
      return recordExceptions;
    }

    public Class<? extends Throwable>[] getIgnoreExceptions() {
      return ignoreExceptions;
    }

    @Override
    public Config clone() {
      Config cloned = null;
      try {
        cloned = (Config) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
      cloned.failureRateThreshold = failureRateThreshold;
      cloned.waitDurationInOpenState = waitDurationInOpenState;
      cloned.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
      cloned.minimumNumberOfCalls = minimumNumberOfCalls;
      cloned.slidingWindowType = slidingWindowType;
      cloned.slidingWindowSize = slidingWindowSize;
      cloned.recordExceptions = recordExceptions;
      cloned.ignoreExceptions = ignoreExceptions;
      return cloned;
    }
  }
}
