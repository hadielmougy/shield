package io.github.shield;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RateLimiterTest {

  private ExecutorService executor;

  @Before
  public void init() {
    executor = Executors.newFixedThreadPool(4);
  }

  @Test
  public void testLimited1() throws InterruptedException {

    final AtomicInteger counter = new AtomicInteger(0);
    Component component = Components.sleepComponentWithCounter(counter, 1000);

    final Component comp = Shield.forObject(component)
        .filter(Filter.rateLimiter()
            .rate(1)
            .build())
        .as(Component.class);

    executor.submit(() -> comp.doCall());
    executor.submit(() -> comp.doCall());

    TimeUnit.MILLISECONDS.sleep(100);

    Assert.assertEquals(1, counter.get());

    executor.shutdown();
  }


  @Test
  public void testLimited2() {
    final AtomicInteger counter = new AtomicInteger(0);
    Component component = Components.sleepComponentWithCounter(counter, 1000);

    final Component comp = Shield.forObject(component)
        .filter(Filter.rateLimiter()
            .rate(2)
            .build())
        .as(Component.class);

    executor.submit(() -> comp.doCall());
    executor.submit(() -> comp.doCall());

    Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> counter.get() == 2);

    Assert.assertEquals(2, counter.get());
    executor.shutdown();
  }


}
