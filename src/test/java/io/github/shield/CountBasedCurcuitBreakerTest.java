package io.github.shield;

import org.junit.Assert;
import org.junit.Test;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class CountBasedCurcuitBreakerTest {


    @Test
    public void testSuccessBreaker() {

        final AtomicInteger counter = new AtomicInteger(0);
        Component component =
                Components.throwingComponentWithCounter(new RuntimeException(), counter, 3);

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
        Assert.assertEquals(4, counter.get());
    }
}
