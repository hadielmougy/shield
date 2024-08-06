package io.github.shield.internal;

import io.github.shield.TimeoutPolicy;
import io.github.shield.util.ExceptionUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RetryInterceptor extends AbstractBaseInterceptor {

  private final int retries;
  private final List<Class<? extends Exception>> exceptions;
  private final TimeoutPolicy timeoutPolicy;
  private final boolean retryOnAll;


  public RetryInterceptor(int retries,
                          List<Class<? extends Exception>> exceptions,
                          TimeoutPolicy timeoutPolicy) {
    this.retries = retries;
    this.exceptions = exceptions;
    this.timeoutPolicy = timeoutPolicy;
    retryOnAll = exceptions.isEmpty();
  }


  @Override
  public boolean beforeInvocation() {
    return true;
  }


  @Override
  public void afterInvocation() {
  }


  @Override
  public Object invoke(Supplier supplier) {
    Object result = null;
    int counter = 1;
    Throwable th = null;
    TimeoutPolicy timeout
        = timeoutPolicy.clone();
    while (counter <= retries) {
      th = null;
      try {
        result = invokeNext(supplier);
        break;
      } catch (Throwable th_) {
        th = th_;
        if (!shouldRetry(th_)) {
          throw th_;
        }
      }
      counter++;
      try {
        timeout.sleep();
      } catch (InterruptedException e) {
        e.printStackTrace();
        return null;
      }
    }

    if (th != null) {
      throw new RetriesExhaustedException(th);
    }
    return result;
  }


  private boolean shouldRetry(final Throwable th) {
    if (retryOnAll) {
      return true;
    }
    for (Class clazz : exceptions) {
      if (th.getClass().equals(clazz) ||
              clazz.isAssignableFrom(th.getClass()) ||
              ExceptionUtil.isClassFoundInStackTrace(th, clazz, 2)) {
        return true;
      }
    }
    return false;
  }



}
