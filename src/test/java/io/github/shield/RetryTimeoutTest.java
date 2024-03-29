package io.github.shield;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class RetryTimeoutTest {

  @Test
  public void shouldTimeout() {
    AtomicInteger atomicInteger = new AtomicInteger(0);
    Component comp = Components.throwingComponentWithCounter(new IllegalStateException(),
        atomicInteger, 5);
    Component decorated = Shield.forObject(comp)
        .filter(Filter.timeout().waitMillis(1100))
        .filter(Filter.retry().delayMillis(1000).maxRetries(5))
        .as(Component.class);

    decorated.doCall();

    Assert.assertEquals(2, atomicInteger.get());
  }
}
