package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeBasedCurcuitBreakerTest {


    @Test
    public void testSuccessBreaker() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        Component component =
                Components.throwingComponentWithCounter(new RuntimeException(), counter, 3);

        final Component comp = Shield.forObject(component)
                .filter(Filter.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(1)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.TIME_BASED))
                .as(Component.class);

        comp.doCall();
        comp.doCall();
        comp.doCall();
        comp.doCall();
        // wait till the window timeout is due
        Thread.sleep(1000);
        // should open after this call
        comp.doCall();
        // should fail
        //comp.doCall();
        Assert.assertEquals(5, counter.get());
    }

    @Test
    public void testHalfOpenFailsBreaker() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        Component component =
                Components.throwingComponentWithCounter(new RuntimeException(), counter, 3);

        final Component comp = Shield.forObject(component)
                .filter(Filter.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(1)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.TIME_BASED))
                .as(Component.class);

        comp.doCall();
        comp.doCall();
        comp.doCall();
        comp.doCall();
        // wait till the window timeout is due
        Thread.sleep(1000);
        // should open after this call
        comp.doCall();
        // should fail
        //comp.doCall();
        // wait till close
        Thread.sleep(1100);
        comp.doCall();
        Assert.assertEquals(6, counter.get());
    }


}
