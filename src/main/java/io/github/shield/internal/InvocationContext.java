package io.github.shield.internal;

import io.github.shield.Interceptor;
import io.github.shield.InvocationCancelledException;
import io.github.shield.InvocationException;

import java.util.function.Supplier;

public final class InvocationContext<T> implements Supplier<T> {


  private final Supplier<T> supplier;

  private final Interceptor firstInterceptor;


  public InvocationContext(Interceptor interceptor, Supplier<T> supplier) {
    this.firstInterceptor = interceptor;
    this.supplier = () -> {
      try {
        return supplier.get();
      } catch (InvocationCancelledException th) {
        throw th;
      } catch (Throwable th) {
        throw new InvocationException(th);
      }
    };
  }



  @Override
  public T get() {
    return firstInterceptor.doInvoke(supplier);
  }
}
