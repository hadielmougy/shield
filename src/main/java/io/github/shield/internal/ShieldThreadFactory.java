package io.github.shield.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ShieldThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String namePrefix;
  public ShieldThreadFactory() {
      AtomicInteger poolNumber = new AtomicInteger(1);
      namePrefix = "shield-" + poolNumber.getAndIncrement() + "-thread-";
  }

  @Override
  public Thread newThread(final Runnable r) {
    Thread t = new Thread(r,
        namePrefix + threadNumber.getAndIncrement());
    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }
}
