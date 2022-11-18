package io.github.shield;

import io.github.shield.internal.BulkheadFilter;
import io.github.shield.internal.CircuitBreakerFilter;
import io.github.shield.internal.Validations;

public interface Bulkhead extends FilterFactory {

  class Config implements Bulkhead {

    @Override
    public Filter build() {
      return new BulkheadFilter();
    }
  }
}
