package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class CountBasedCurcuitBreakerTest {


    @Test
    public void testSuccessBreaker() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        Component component =
                Components.throwingComponentWithCounter(new RuntimeException(), counter, 2);

        final Component comp = Shield.forObject(component)
                .filter(Filter.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(4)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .as(Component.class);

        comp.doCall();
        comp.doCall();
        comp.doCall();
        comp.doCall();
        // wait till the circuit closes
        Thread.sleep(1100);
        comp.doCall();
        Assert.assertEquals(5, counter.get());
    }

    @Test
    public void testHalfOpenFailsBreaker() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        Component component =
                Components.throwingComponentWithCounter(new RuntimeException(), counter, 20);

        final Component comp = Shield.forObject(component)
                .filter(Filter.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(4)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .as(Component.class);

        comp.doCall();
        comp.doCall();
        comp.doCall();
        comp.doCall();
        // wait till the circuit closes
        Thread.sleep(1100);
        comp.doCall();
        Assert.assertEquals(5, counter.get());
    }


}
