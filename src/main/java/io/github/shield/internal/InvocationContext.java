package io.github.shield.internal;

import io.github.shield.Interceptor;

import java.util.function.Supplier;

public final class InvocationContext<T> implements Supplier<T> {


  private final Supplier<T> supplier;

  private final Interceptor firstInterceptor;


  public InvocationContext(Interceptor interceptor, Supplier<T> supplier) {
    this.firstInterceptor = interceptor;
    this.supplier = supplier;
  }



  @Override
  public T get() {
    return firstInterceptor.doInvoke(supplier);
  }
}
