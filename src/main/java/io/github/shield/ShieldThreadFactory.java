package io.github.shield;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ShieldThreadFactory implements ThreadFactory {

  /**
   *
   */
  private final AtomicInteger poolNumber = new AtomicInteger(1);
  /**
   *
   */
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  /**
   *
   */
  private final String namePrefix;

  /**
   * Constructor for thread factory.
   */
  public ShieldThreadFactory() {
    namePrefix = "shield-" + poolNumber.getAndIncrement() + "-thread-";
  }

  /**
   * {@inheritDoc}
   */
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
