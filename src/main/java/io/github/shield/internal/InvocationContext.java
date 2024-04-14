package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.InvocationCancelledException;
import io.github.shield.InvocationException;

import java.util.function.Supplier;

public final class InvocationContext<T> implements Supplier<T> {


  private final Supplier<T> supplier;

  private final Filter firstFilter;


  public InvocationContext(Filter filter, Supplier<T> supplier) {
    this.firstFilter = filter;
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
    return firstFilter.doInvoke(supplier);
  }
}
