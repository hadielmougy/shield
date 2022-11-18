package io.github.shield;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComponentFallbackTest {

  private ExecutorService executor;

  @Before
  public void init() {
    executor = Executors.newFixedThreadPool(4);
  }

  @Test(expected = InvocationCancelledException.class)
  public void testThrottledAndFallback() throws InterruptedException {
    final AtomicInteger counter = new AtomicInteger(0);

    Component targetObj = Components.sleepComponentWithCounter(counter, 2000);

    final Component comp = Shield.forObject(targetObj)
        .filter(Filter.throttler()
            .requests(1)
            .maxWaitMillis(500))
        .as(Component.class);

    executor.submit(() -> comp.doCall());

    Thread.currentThread().sleep(500);

    comp.doCall();

    Awaitility.await().until(() -> counter.get() == 1);
    Assert.assertEquals(1, counter.get());

    executor.shutdown();
  }

}
