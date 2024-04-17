package io.github.shield.internal;

import io.github.shield.Interceptor;

import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;


public abstract class AbstractBaseInterceptor implements Interceptor {

  /**
   * Next filter.
   */
  private Interceptor next;

  /**
   *
   */
  protected ExecutorService executorService;


  /**
   * Context holder.
   */
  private ThreadLocal<InvocationContext> context = new ThreadLocal<>();


  public AbstractBaseInterceptor() {
    configureExecutor(ExecutorConfigurer.INSTANCE);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Interceptor o) {
    return this.getOrder().compareTo(o.getOrder());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getOrder() {
    return 0;
  }


  /**
   * {@inheritDoc}
   */
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


  /**
   * {@inheritDoc}
   */
  @Override
  public void setContext(final InvocationContext ctx) {
    this.context.set(ctx);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public InvocationContext getContext() {
    return context.get();
  }

  /**
   * {@inheritDoc}
   *
   * @param supplier
   */
  @Override
  public Object invoke(Supplier supplier) {
    return invokeNext(supplier);
  }


  @VisibleForTest
  private void setExecutorService(final ExecutorService exe) {
    this.executorService = exe;
  }

}
