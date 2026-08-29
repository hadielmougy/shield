package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;

/**
 * Validation tests for the {@link CircuitBreaker} builder.
 */
public class CircuitBreakerConfigTest {

    @Test
    public void rejectsNonPositiveFailureRateThreshold() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.circuitBreaker().failureRateThreshold(0));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.circuitBreaker().failureRateThreshold(-1));
    }

    @Test
    public void rejectsNonPositiveSlidingWindowSize() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.circuitBreaker().slidingWindowSize(0));
    }

    @Test
    public void rejectsNonPositivePermittedCallsInHalfOpenState() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.circuitBreaker().permittedNumberOfCallsInHalfOpenState(0));
    }

    @Test
    public void rejectsNonPositiveMinimumNumberOfCalls() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.circuitBreaker().minimumNumberOfCalls(0));
    }

    @Test
    public void rejectsNullWaitDuration() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.circuitBreaker().waitDurationInOpenState(null));
    }

    @Test
    public void rejectsNullWindowType() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.circuitBreaker().slidingWindowType(null));
    }

    @Test
    public void buildsCountBasedInterceptorWithValidConfig() {
        Assert.assertNotNull(Interceptor.circuitBreaker()
                .failureRateThreshold(50)
                .slidingWindowSize(4)
                .waitDurationInOpenState(Duration.ofSeconds(1))
                .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED)
                .build());
    }

    @Test
    public void buildsTimeBasedInterceptorWithValidConfig() {
        Assert.assertNotNull(Interceptor.circuitBreaker()
                .failureRateThreshold(50)
                .slidingWindowSize(1)
                .minimumNumberOfCalls(1)
                .waitDurationInOpenState(Duration.ofSeconds(1))
                .slidingWindowType(CircuitBreaker.WindowType.TIME_BASED)
                .build());
    }
}