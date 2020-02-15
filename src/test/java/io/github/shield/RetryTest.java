package io.github.shield;

import io.github.shield.internal.RetriesExhaustedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class RetryTest {

    private Shield shield;

    @Before
    public void init() {
        shield = new Shield();
    }

    @Test
    public void shouldRetry() {
        shield.addFilter(Filter.retry().timeUnit(TimeUnit.MILLISECONDS).withDelay(500).withMaxRetries(3).build());
        final AtomicInteger counter = new AtomicInteger(0);
        Component component = new TestComponentWithFallback(() -> {
            int count = counter.incrementAndGet();
            if (count < 2) {
                throw new RuntimeException();
            }
        }, ()-> counter.decrementAndGet());

        final Component comp = shield.forObject(component).as(Component.class);

        comp.doCall();

        Assert.assertEquals(2, counter.get());
    }



    @Test(expected = RetriesExhaustedException.class)
    public void shouldRetryAndExhaustRetries() {
        shield.addFilter(Filter.retry().timeUnit(TimeUnit.MILLISECONDS).withDelay(500).withMaxRetries(3).build());
        final AtomicInteger counter = new AtomicInteger(0);
        Component component = new TestComponentWithFallback(() -> {
            throw new RuntimeException();
        }, ()-> counter.decrementAndGet());

        final Component comp = shield.forObject(component).as(Component.class);

        comp.doCall();
    }


    @Test
    public void shouldRetryOnGivenException() {
        Shield shield = new Shield();
        shield.addFilter(Filter.retry().timeUnit(TimeUnit.MILLISECONDS).withDelay(500).withMaxRetries(3)
                .onException(IllegalArgumentException.class).build());
        final AtomicInteger counter = new AtomicInteger(0);
        Component component = new TestComponentWithFallback(() -> {
            int count = counter.incrementAndGet();
            if (count < 2) {
                throw new IllegalArgumentException();
            }
        }, ()-> counter.decrementAndGet());

        final Component comp = shield.forObject(component).as(Component.class);

        comp.doCall();

        Assert.assertEquals(2, counter.get());
    }



    @Test(expected = RetriesExhaustedException.class)
    public void shouldNotRetryOnGivenException() {
        shield.addFilter(Filter.retry().timeUnit(TimeUnit.MILLISECONDS).withDelay(500).withMaxRetries(3)
                .onException(IllegalArgumentException.class).build());
        final AtomicInteger counter = new AtomicInteger(0);
        Component component = new TestComponentWithFallback(() -> {
            throw new IllegalStateException();
        }, ()-> counter.decrementAndGet());

        final Component comp = shield.forObject(component).as(Component.class);

        comp.doCall();
    }
}
