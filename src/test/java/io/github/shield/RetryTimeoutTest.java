package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public class RetryTimeoutTest {

  @Test
  public void shouldTimeout() {
    AtomicInteger atomicInteger = new AtomicInteger(0);
    Supplier<Void> comp = Components.throwingComponentWithCounter(new IllegalStateException(),
        atomicInteger, 5);
    Supplier<Void> decorated = Shield.wrapSupplier(comp)
        .filter(Filter.timeout().waitMillis(1100))
        .filter(Filter.retry().delayMillis(1000).maxRetries(5))
        .build();

    decorated.get();

    Assert.assertEquals(2, atomicInteger.get());
  }
}
