package io.github.shield;

import io.github.shield.internal.RetriesExhaustedException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public class RetryTest {

  private static class ThrowableRuntimeException extends RuntimeException {
    public ThrowableRuntimeException() {
      super();
    }

    public ThrowableRuntimeException(RuntimeException cause) {
      super(cause);
    }
  }
  private static class ThrowableRuntimeException2 extends IllegalArgumentException {}


  @Test
  public void shouldRetry() {
    final AtomicInteger counter = new AtomicInteger(0);
    Supplier<Void> target = Suppliers.throwingSupplierWithCounter(new RuntimeException(), counter,
        1);
    final Supplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.retry()
            .delayMillis(500)
            .maxRetries(3))
        .build();
    comp.get();
    Assert.assertEquals(2, counter.get());
  }

  @Test(expected = RetriesExhaustedException.class)
  public void shouldRetry2() {
    Supplier<Void> target = Suppliers.throwingSupplier(new ThrowableRuntimeException());
    final Supplier<Void> comp = Shield.decorate(target)
            .with(Interceptor.retry()
                    .delayMillis(500)
                    .onException(RuntimeException.class)
                    .maxRetries(3))
            .build();
    comp.get();
  }

  @Test(expected = RetriesExhaustedException.class)
  public void shouldRetry3() {
    Supplier<Void> target = Suppliers.throwingSupplier(
            new ThrowableRuntimeException(new ThrowableRuntimeException2()));
    final Supplier<Void> comp = Shield.decorate(target)
            .with(Interceptor.retry()
                    .delayMillis(500)
                    .onException(IllegalArgumentException.class)
                    .maxRetries(3))
            .build();
    comp.get();
  }



  @Test(expected = RetriesExhaustedException.class)
  public void shouldRetryAndExhaustRetries() {
    Supplier<Void> target = Suppliers.throwingSupplier(new RuntimeException());
    final Supplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.retry()
            .delayMillis(500)
            .maxRetries(3))
        .build();
    comp.get();
  }


  @Test
  public void shouldRetryOnGivenException() {
    final AtomicInteger counter = new AtomicInteger(0);
    final Supplier<Void> target = Suppliers.throwingSupplierWithCounter(
        new IllegalArgumentException(), counter, 1);
    final Supplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class))
        .build();
    comp.get();
    Assert.assertEquals(2, counter.get());
  }


  @Test(expected = IllegalStateException.class)
  public void shouldNotRetryOnTheGivenException() {
    final Supplier<Void> target = Suppliers.throwingSupplier(new IllegalStateException());

    final Retry retry = Interceptor.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class);

    final Supplier<Void> decorated = Shield.decorate(target)
            .with(retry)
            .build();

    decorated.get();
  }
}
