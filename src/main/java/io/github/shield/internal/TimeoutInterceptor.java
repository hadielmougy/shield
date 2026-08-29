package io.github.shield.internal;


import io.github.shield.ExecutorProvider;
import io.github.shield.InvocationCancelledException;
import io.github.shield.TimeoutExceededException;
import io.github.shield.util.ExceptionUtil;

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
      // copy context to the worker thread, and clear it afterwards so the pooled thread does
      // not retain the invocation context between tasks
      setContext(context);
      try {
        return invokeNext(supplier);
      } finally {
        clearContext();
      }
    });
    try {
      return future.get(maxWait, timeunit);
    } catch (TimeoutException ex) {
      future.cancel(true);
      throw new TimeoutExceededException(
          "Invocation did not complete within " + maxWait + " " + timeunit);
    } catch (InterruptedException e) {
      future.cancel(true);
      Thread.currentThread().interrupt();
      throw new InvocationCancelledException("Thread interrupted while awaiting invocation result");
    } catch (ExecutionException e) {
      future.cancel(true);
      // propagate the original failure thrown by the target invocation
      Throwable cause = e.getCause() != null ? e.getCause() : e;
      throw ExceptionUtil.sneakyThrow(cause);
    }
  }

  @Override
  public void afterInvocation() {
  }

  @Override
  public void configureExecutor(final ExecutorProvider executorProvider) {
    this.executorService = executorProvider.get(this);
  }


}
