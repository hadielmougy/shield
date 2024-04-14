package io.github.shield;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class FireAndForgetConnectorTest {


  private ExecutorService executor;

  @Before
  public void init() {
    executor = Executors.newFixedThreadPool(4);
  }

  @Test
  public void testRunningInDifferentThread() throws InterruptedException {
    final StringBuilder stringBuilder = new StringBuilder();
    Supplier<StringBuilder> target = () -> stringBuilder.append(Thread.currentThread().getName());
    final Supplier<StringBuilder> comp = Shield.decorate(target)
        .with(Filter.fireAndForget())
        .build();
    comp.get();
    TimeUnit.MILLISECONDS.sleep(100);
    String currentThreadName = Thread.currentThread().getName();
    Assert.assertNotEquals("", stringBuilder.toString());
    Assert.assertNotEquals(currentThreadName, stringBuilder.toString());
    executor.shutdown();

  }


}
