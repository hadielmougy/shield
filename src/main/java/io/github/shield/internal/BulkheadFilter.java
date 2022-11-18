package io.github.shield.internal;

import io.github.shield.ExecutorProvider;
import io.github.shield.Filter;
import java.util.function.Supplier;

public class BulkheadFilter implements Filter {

  @Override
  public boolean beforeInvocation() {
    return false;
  }

  @Override
  public void afterInvocation() {

  }

  @Override
  public Integer getOrder() {
    return null;
  }

  @Override
  public void setNext(Filter next) {

  }

  @Override
  public Object invoke(Supplier supplier) {
    return null;
  }

  @Override
  public void setContext(InvocationContext context) {

  }

  @Override
  public InvocationContext getContext() {
    return null;
  }

  @Override
  public Object doInvoke(Supplier supplier) {
    return Filter.super.doInvoke(supplier);
  }

  @Override
  public void configureExecutor(ExecutorProvider executorProvider) {
    Filter.super.configureExecutor(executorProvider);
  }

  @Override
  public int compareTo(Filter o) {
    return 0;
  }
}
