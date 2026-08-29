package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

/**
 * Validation of the throttler and rate-limiter builders. These previously validated the wrong
 * variable and silently accepted non-positive values.
 */
public class LimiterConfigValidationTest {

    @Test
    public void throttlerRejectsNonPositiveRequests() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.throttler().requests(0));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.throttler().requests(-1));
    }

    @Test
    public void throttlerRejectsNonPositiveMaxWait() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.throttler().maxWaitMillis(0));
    }

    @Test
    public void rateLimiterRejectsNonPositiveRate() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.rateLimiter().rate(0));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.rateLimiter().rate(-5));
    }

    @Test
    public void validValuesAreAccepted() {
        Assert.assertNotNull(Interceptor.throttler().requests(3).maxWaitMillis(100).build());
        Assert.assertNotNull(Interceptor.rateLimiter().rate(5).build());
    }
}