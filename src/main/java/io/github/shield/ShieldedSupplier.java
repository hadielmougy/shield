package io.github.shield;

import java.util.function.Supplier;

/**
 * A decorated {@link Supplier} returned by {@link Shield#build()}.
 *
 * <p>It is also {@link AutoCloseable}: closing it releases any resources (such as the thread pools
 * used by the timeout and rate-limiter interceptors) held by the underlying interceptor chain.
 * Callers that build long-lived decorated suppliers should close them when done, e.g. with
 * try-with-resources. {@link #close()} is idempotent and never throws a checked exception.
 */
public interface ShieldedSupplier<T> extends Supplier<T>, AutoCloseable {

  @Override
  void close();
}
