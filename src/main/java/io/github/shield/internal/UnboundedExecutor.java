package io.github.shield.internal;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UnboundedExecutor extends ThreadPoolExecutor {

  public UnboundedExecutor() {
    super(4, 1000, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
  }


  @Override
  protected void beforeExecute(Thread t, Runnable r) {
    super.beforeExecute(t, r);
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
  }
}
