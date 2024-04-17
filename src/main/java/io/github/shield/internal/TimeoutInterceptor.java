package io.github.shield.internal;


import io.github.shield.ExecutorProvider;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class TimeoutInterceptor extends AbstractBaseInterceptor {

  private final long maxWait;
  private final TimeUnit timeunit;

  public TimeoutInterceptor(final long wait, final TimeUnit unit) {
    this.maxWait = wait;
    this.timeunit = unit;
  }

  @Override
  public boolean beforeInvocation() {
    return true;
  }

  @Override
  public Object invoke(Supplier supplier) {
    InvocationContext context = getContext();
    Future<Object> future = executorService.submit(() -> {
      // copy context to the new thread
      setContext(context);
      return invokeNext(supplier);
    });
    try {
      return future.get(maxWait, timeunit);
    } catch (TimeoutException ex) {
      // handle the timeout
    } catch (InterruptedException e) {
      // handle the interrupts
    } catch (ExecutionException e) {
      // handle other exceptions
    } finally {
      future.cancel(true);
    }
    return null;
  }

  @Override
  public void afterInvocation() {
  }

  @Override
  public void configureExecutor(final ExecutorProvider executorProvider) {
    this.executorService = executorProvider.get(this);
  }


}
