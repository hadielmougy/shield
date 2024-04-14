package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TimeBasedCurcuitBreakerTest {


    @Test
    public void testSuccessBreaker() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        Supplier<Void> target =
                Suppliers.throwingSupplierWithCounter(new RuntimeException(), counter, 3);
        final Supplier<Void> comp = Shield.decorate(target)
                .with(Filter.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(1)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.TIME_BASED))
                .build();
        comp.get();
        comp.get();
        comp.get();
        comp.get();
        // wait till the window timeout is due
        Thread.sleep(1000);
        // should open after this call
        comp.get();
        // should fail
        //comp.doCall();
        Assert.assertEquals(5, counter.get());
    }

    @Test
    public void testHalfOpenFailsBreaker() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        Supplier<Void> component =
                Suppliers.throwingSupplierWithCounter(new RuntimeException(), counter, 3);
        final Supplier<Void> comp = Shield.decorate( component)
                .with(Filter.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(1)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.TIME_BASED))
                .build();
        comp.get();
        comp.get();
        comp.get();
        comp.get();
        // wait till the window timeout is due
        Thread.sleep(1000);
        // should open after this call
        comp.get();
        // should fail
        //comp.doCall();
        // wait till close
        Thread.sleep(1100);
        comp.get();
        Assert.assertEquals(6, counter.get());
    }
}
