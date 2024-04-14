package io.github.shield;

import io.github.shield.internal.RetriesExhaustedException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class RetryTest {


  @Test
  public void shouldRetry() {
    final AtomicInteger counter = new AtomicInteger(0);

    Component component = Components.throwingComponentWithCounter(new RuntimeException(), counter,
        1);

    final Component comp = Shield.forObject(Component.class, component)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3))
        .build();

    comp.doCall();

    Assert.assertEquals(2, counter.get());
  }


  @Test(expected = RetriesExhaustedException.class)
  public void shouldRetryAndExhaustRetries() {

    Component component = Components.throwingComponent(new RuntimeException());

    final Component comp = Shield.forObject(Component.class, component)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3))
        .build();

    comp.doCall();
  }


  @Test
  public void shouldRetryOnGivenException() {
    final AtomicInteger counter = new AtomicInteger(0);
    final Component component = Components.throwingComponentWithCounter(
        new IllegalArgumentException(), counter, 1);
    final Component comp = Shield.forObject(Component.class, component)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class))
        .build();

    comp.doCall();

    Assert.assertEquals(2, counter.get());
  }


  @Test(expected = RetriesExhaustedException.class)
  public void shouldNotRetryOnGivenException() {
    Component component = Components.throwingComponent(new IllegalStateException());

    final Component comp = Shield.forObject(Component.class, component)
        .filter(Filter.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class))
        .build();

    comp.doCall();
  }
}
