package io.github.shield.internal;

import io.github.shield.ExecutorProvider;

import java.util.function.Supplier;


public
class FireAndForgetInterceptor extends AbstractBaseInterceptor {

  @Override
  public void configureExecutor(final ExecutorProvider executorProvider) {
    executorService = executorProvider.get(this);
  }

  @Override
  public boolean beforeInvocation() {
    return true;
  }

  @Override
  public Object invoke(Supplier supplier) {
    ensureExecutor();
    InvocationContext context = getContext();
    executorService.submit(() -> {
      // copy context to the new thread
      setContext(context);
      return invokeNext(supplier);
    });
    return null;
  }

  @Override
  public void afterInvocation() {
  }


  private void ensureExecutor() {
    if (executorService == null) {
      final String msg = "executor service is not configured";
      throw new IllegalStateException(msg);
    }
  }


}
