package io.github.shield.internal;

import io.github.shield.Interceptor;

import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;


public abstract class AbstractBaseInterceptor implements Interceptor {

  private Interceptor next;
  protected ExecutorService executorService;
  private final ThreadLocal<InvocationContext> context = new ThreadLocal<>();


  public AbstractBaseInterceptor() {
    configureExecutor(ExecutorConfigurer.INSTANCE);
  }


  @Override
  public int compareTo(final Interceptor o) {
    return this.getOrder().compareTo(o.getOrder());
  }


  @Override
  public Integer getOrder() {
    return 0;
  }

  @Override
  public void setNext(final Interceptor n) {
    this.next = n;
  }


  protected final Object invokeNext(Supplier supplier) {
    if (next == null) {
      return supplier.get();
    }
    return next.doInvoke(supplier);
  }


  @Override
  public void setContext(final InvocationContext ctx) {
    this.context.set(ctx);
  }


  @Override
  public InvocationContext getContext() {
    return context.get();
  }

  @Override
  public Object invoke(Supplier supplier) {
    return invokeNext(supplier);
  }


}
