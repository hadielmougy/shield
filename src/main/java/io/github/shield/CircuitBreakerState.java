package io.github.shield;

import java.util.function.Supplier;

public interface CircuitBreakerState {

    Object invoke(Supplier<?> supplier);
}
