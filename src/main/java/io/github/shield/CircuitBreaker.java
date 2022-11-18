package io.github.shield;

import io.github.shield.internal.CircuitBreakerFilter;
import io.github.shield.internal.Validations;

public interface CircuitBreaker extends FilterFactory {

  enum Type {SEMAPHORE, THREAD_POOL}

  CircuitBreaker type(Type type);

  class Config implements CircuitBreaker {

    @Override
    public CircuitBreaker type(Type type) {
      Validations.checkArgument(type != null,
          "Type can't be null"
      );
      return this;
    }

    @Override
    public Filter build() {
      return new CircuitBreakerFilter();
    }
  }
}
