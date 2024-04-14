package io.github.shield;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class RateLimiterTest {

  private ExecutorService executor;

  @Before
  public void init() {
    executor = Executors.newFixedThreadPool(4);
  }

  @Test
  public void testLimited1() throws InterruptedException {
    final AtomicInteger counter = new AtomicInteger(0);
    final Supplier<Void> target = Suppliers.sleepSupplierWithCounter(counter, 1000);
    final Supplier<Void> comp = Shield.decorate(target)
        .with(Filter.rateLimiter()
            .rate(1))
        .build();
    executor.submit(comp::get);
    executor.submit(comp::get);
    TimeUnit.MILLISECONDS.sleep(100);
    Assert.assertEquals(1, counter.get());
    executor.shutdown();
  }


  @Test
  public void testLimited2() {
    final AtomicInteger counter = new AtomicInteger(0);
    Supplier<Void> target = Suppliers.sleepSupplierWithCounter(counter, 1000);
    final Supplier<Void> comp = Shield.decorate(target)
        .with(Filter.rateLimiter().rate(2))
        .build();
    executor.submit(comp::get);
    executor.submit(comp::get);
    Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> counter.get() == 2);
    Assert.assertEquals(2, counter.get());
    executor.shutdown();
  }


}
