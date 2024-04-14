package io.github.shield;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Components {


  public static Supplier<Void> sleepComponentWithCounter(AtomicInteger counter, long sleepMillis) {
    return () -> {
      counter.incrementAndGet();
      try {
        Thread.currentThread().sleep(sleepMillis);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
        return null;
    };
  }


  public static Supplier<Void> throwingComponentWithCounter(RuntimeException e, AtomicInteger counter, int maxCount) {
    return () -> {
      int count = counter.incrementAndGet();
      if (count <= maxCount) {
        throw e;
      }
        return null;
    };
  }


  public static Supplier<Void> throwingComponent(RuntimeException e) {
    return () -> {
      throw e;
    };
  }

}
