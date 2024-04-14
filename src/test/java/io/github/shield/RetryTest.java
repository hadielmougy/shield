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

    Supplier<Void> component = Components.throwingComponentWithCounter(new RuntimeException(), counter,
        1);

    final Supplier<Void> comp = Shield.wrapSupplier(component)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3))
        .build();

    comp.get();

    Assert.assertEquals(2, counter.get());
  }


  @Test(expected = RetriesExhaustedException.class)
  public void shouldRetryAndExhaustRetries() {

    Supplier<Void> component = Components.throwingComponent(new RuntimeException());

    final Supplier<Void> comp = Shield.wrapSupplier(component)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3))
        .build();

    comp.get();
  }


  @Test
  public void shouldRetryOnGivenException() {
    final AtomicInteger counter = new AtomicInteger(0);
    final Supplier<Void> component = Components.throwingComponentWithCounter(
        new IllegalArgumentException(), counter, 1);
    final Supplier<Void> comp = Shield.wrapSupplier( component)
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
    Supplier<Void> component = Components.throwingComponent(new IllegalStateException());

    final Supplier<Void> comp = Shield.wrapSupplier(component)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class))
        .build();

    comp.get();
  }
}
