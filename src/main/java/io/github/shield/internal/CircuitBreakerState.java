package io.github.shield.internal;

import java.util.function.Supplier;

public interface CircuitBreakerState {

    Object invoke(Supplier supplier);
}
