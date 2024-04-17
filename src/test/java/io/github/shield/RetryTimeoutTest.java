package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public class RetryTimeoutTest {

  @Test
  public void shouldTimeout() {
    final AtomicInteger atomicInteger = new AtomicInteger(0);
    final Supplier<Void> target = Suppliers.throwingSupplierWithCounter(new IllegalStateException(),
        atomicInteger, 5);
    Supplier<Void> decorated = Shield.decorate(target)
        .with(Interceptor.timeout().waitMillis(1100))
        .with(Interceptor.retry().delayMillis(1000).maxRetries(5))
        .build();
    decorated.get();
    Assert.assertEquals(2, atomicInteger.get());
  }
}
