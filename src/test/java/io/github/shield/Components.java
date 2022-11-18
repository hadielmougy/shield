package io.github.shield;

import java.util.concurrent.atomic.AtomicInteger;

public class Components {


  public static Component sleepComponentWithCounter(AtomicInteger counter, long sleepMillis) {
    return new TestComponentWithFallback(() -> {
      counter.incrementAndGet();
      try {
        Thread.currentThread().sleep(sleepMillis);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }, () -> counter.decrementAndGet());
  }


  public static Component throwingComponentWithCounter(RuntimeException e, AtomicInteger counter,
      int maxCount) {
    return new TestComponentWithFallback(() -> {
      int count = counter.incrementAndGet();
      if (count < maxCount) {
        throw e;
      }
    }, () -> counter.decrementAndGet());
  }


  public static Component throwingComponent(RuntimeException e) {
    return new TestComponentWithFallback(() -> {
      throw e;
    }, null);
  }

}
