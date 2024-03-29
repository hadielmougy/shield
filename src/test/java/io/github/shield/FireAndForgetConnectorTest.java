package io.github.shield;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FireAndForgetConnectorTest {


  private ExecutorService executor;

  @Before
  public void init() {
    executor = Executors.newFixedThreadPool(4);
  }

  @Test
  public void testRunningInDifferentThread() throws InterruptedException {

    final StringBuilder stringBuilder = new StringBuilder();

    TestComponentWithFallback targetObj = new TestComponentWithFallback(
        () -> stringBuilder.append(Thread.currentThread().getName()), null);
    final Component comp = Shield.forObject(targetObj)
        .filter(Filter.fireAndForget())
        .as(Component.class);

    comp.doCall();

    TimeUnit.MILLISECONDS.sleep(100);
    String currentThreadName = Thread.currentThread().getName();
    Assert.assertNotEquals("", stringBuilder.toString());
    Assert.assertNotEquals(currentThreadName, stringBuilder.toString());
    executor.shutdown();

  }


}
