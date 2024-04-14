package io.github.shield;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ComponentFallbackTest {

  private ExecutorService executor;

  @Before
  public void init() {
    executor = Executors.newFixedThreadPool(4);
  }

  @Test(expected = InvocationCancelledException.class)
  public void testThrottledAndFallback() throws InterruptedException {
    final AtomicInteger counter = new AtomicInteger(0);

    Supplier<Void> targetObj = Components.sleepComponentWithCounter(counter, 2000);

    final Supplier<Void> comp = Shield.wrapSupplier(targetObj)
        .filter(Filter.throttler()
            .requests(1)
            .maxWaitMillis(500))
        .build();

    executor.submit(comp::get);

    Thread.currentThread().sleep(500);

    comp.get();

    Awaitility.await().until(() -> counter.get() == 1);
    Assert.assertEquals(1, counter.get());

    executor.shutdown();
  }

}
