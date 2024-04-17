package io.github.shield;


import io.github.shield.internal.InvocationContext;

import java.util.function.Supplier;

/**
 * Base build type that represents that invocation of a method from a client component to the target
 * (this). The method is represented as a supplier
 */
public interface Interceptor extends Comparable<Interceptor> {


  /**
   * Factory method for rate limiting build.
   *
   * @return rate limiter build factory
   */
  static RateLimiter rateLimiter() {
    return new RateLimiter.Config();
  }

  /**
   * Factory method for throttling build.
   *
   * @return throttler build factory
   */
  static Throttler throttler() {
    return new Throttler.Config();
  }

  /**
   * Factory method for fire and forget build.
   *
   * @return direct build factory
   */
  static FireAndForget fireAndForget() {
    return new FireAndForget.Config();
  }


  /**
   * Factory method for retry build.
   *
   * @return direct build factory
   */
  static Retry retry() {
    return new Retry.Config();
  }


  /**
   * Factory method for time build.
   *
   * @return direct build factory
   */
  static Timeout timeout() {
    return new Timeout.Config();
  }

  /**
   * Factory method for circuit breaker build.
   *
   * @return direct build factory
   */
  static CircuitBreaker circuitBreaker() {
    return new CircuitBreaker.Config();
  }

  /**
   * This should be implemented by the build type. It contains all build specific logic to acquire
   * needed resources before the invocation, like limiting requests counting requests etc.
   *
   * @return returns the target components return value
   */
  boolean beforeInvocation();


  /**
   * This should be implemented by the build to close all acquired resources.
   */
  void afterInvocation();

  /**
   * order of this filter instance in the execution chain.
   *
   * @return the number that indicates rhe order
   */
  Integer getOrder();


  /**
   * Next filter of execution chain.
   *
   * @param next next filter
   */
  void setNext(Interceptor next);


  /**
   * Invoke this filter.
   *
   * @param supplier
   * @return result of execution
   */
  <T> T invoke(Supplier<T> supplier);


  /**
   * Add execution context to the filter instance.
   *
   * @param context execution context
   */
  <T> void setContext(InvocationContext<T> context);


  /**
   * Return invocation context.
   *
   * @return context
   */
  <T> InvocationContext<T> getContext();

  /**
   * Invocation Template.
   *
   * @param supplier
   * @return invocation result
   */
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


  /**
   * Overriden if need executor to be configured for this instance.
   *
   * @param executorProvider
   */
  default void configureExecutor(ExecutorProvider executorProvider) {

  }
}
