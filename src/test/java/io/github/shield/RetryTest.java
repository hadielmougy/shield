package io.github.shield;

import io.github.shield.internal.RetriesExhaustedException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public class RetryTest {


  @Test
  public void shouldRetry() {
    final AtomicInteger counter = new AtomicInteger(0);
    Supplier<Void> target = Suppliers.throwingSupplierWithCounter(new RuntimeException(), counter,
        1);
    final Supplier<Void> comp = Shield.wrapSupplier(target)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3))
        .build();
    comp.get();
    Assert.assertEquals(2, counter.get());
  }


  @Test(expected = RetriesExhaustedException.class)
  public void shouldRetryAndExhaustRetries() {
    Supplier<Void> target = Suppliers.throwingSupplier(new RuntimeException());
    final Supplier<Void> comp = Shield.wrapSupplier(target)
        .filter(Filter.retry()
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
    final Supplier<Void> comp = Shield.wrapSupplier(target)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class))
        .build();
    comp.get();
    Assert.assertEquals(2, counter.get());
  }


  @Test(expected = RetriesExhaustedException.class)
  public void shouldNotRetryOnGivenException() {
    final Supplier<Void> target = Suppliers.throwingSupplier(new IllegalStateException());
    final Supplier<Void> comp = Shield.wrapSupplier(target)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class))
        .build();
    comp.get();
  }
}
