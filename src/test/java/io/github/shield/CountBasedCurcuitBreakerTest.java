package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class CountBasedCurcuitBreakerTest {


    @Test
    public void testSuccessBreaker() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        Supplier<Void> component =
                Components.throwingComponentWithCounter(new RuntimeException(), counter, 2);

        final Supplier<Void> comp = Shield.wrapSupplier( component)
                .filter(Filter.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(4)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        comp.get();
        comp.get();
        comp.get();
        comp.get();
        // wait till the circuit closes
        Thread.sleep(1100);
        comp.get();
        Assert.assertEquals(5, counter.get());
    }

    @Test
    public void testHalfOpenFailsBreaker() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        Supplier<Void> component =
                Components.throwingComponentWithCounter(new RuntimeException(), counter, 20);

        final Supplier<Void> comp = Shield.wrapSupplier( component)
                .filter(Filter.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(4)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

        comp.get();
        comp.get();
        comp.get();
        comp.get();
        // wait till the circuit closes
        Thread.sleep(1100);
        comp.get();
        Assert.assertEquals(5, counter.get());
    }


}
