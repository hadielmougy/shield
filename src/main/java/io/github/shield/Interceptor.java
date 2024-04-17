package io.github.shield;


import io.github.shield.internal.InvocationContext;

import java.util.function.Supplier;

public interface Interceptor extends Comparable<Interceptor> {


  static RateLimiter rateLimiter() {
    return new RateLimiter.Config();
  }


  static Throttler throttler() {
    return new Throttler.Config();
  }

  static FireAndForget fireAndForget() {
    return new FireAndForget.Config();
  }


  static Retry retry() {
    return new Retry.Config();
  }

  static Timeout timeout() {
    return new Timeout.Config();
  }

  static CircuitBreaker circuitBreaker() {
    return new CircuitBreaker.Config();
  }

  boolean beforeInvocation();


  void afterInvocation();

  Integer getOrder();

  void setNext(Interceptor next);

  <T> T invoke(Supplier<T> supplier);

  <T> void setContext(InvocationContext<T> context);


  <T> InvocationContext<T> getContext();

  default <T> T doInvoke(Supplier<T> supplier) {
    T result = null;
    final boolean success = beforeInvocation();
    if (!success) {
      throw new InvocationCancelledException("Can't invoke target supplier");
    }

    try {
      result = invoke(supplier);
    } finally {
      afterInvocation();
    }

    return result;
  }


  default void configureExecutor(ExecutorProvider executorProvider) {

  }
}
