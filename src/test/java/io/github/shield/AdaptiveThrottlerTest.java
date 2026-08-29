package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class AdaptiveThrottlerTest {

    @Test
    public void passesResultThroughWhenAdmitted() {
        final AtomicInteger counter = new AtomicInteger(0);
        Supplier<String> target = () -> {
            counter.incrementAndGet();
            return "ok";
        };
        try (ShieldedSupplier<String> comp = Shield.decorate(target)
                .with(Interceptor.adaptiveThrottler().requests(4).minRequests(1).backoffRatio(0.5))
                .build()) {
            Assert.assertEquals("ok", comp.get());
            Assert.assertEquals(1, counter.get());
        }
    }

    @Test
    public void propagatesTargetException() {
        Supplier<String> failing = () -> {
            throw new IllegalStateException("boom");
        };
        try (ShieldedSupplier<String> comp = Shield.decorate(failing)
                .with(Interceptor.adaptiveThrottler().requests(4))
                .build()) {
            IllegalStateException thrown =
                    Assert.assertThrows(IllegalStateException.class, comp::get);
            Assert.assertEquals("boom", thrown.getMessage());
        }
    }

    @Test
    public void configValidation() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.adaptiveThrottler().requests(0));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.adaptiveThrottler().minRequests(0));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.adaptiveThrottler().maxWaitMillis(0));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.adaptiveThrottler().backoffRatio(0));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.adaptiveThrottler().backoffRatio(1));
        // min greater than max is rejected at build time
        Assert.assertThrows(IllegalArgumentException.class,
                () -> Interceptor.adaptiveThrottler().requests(2).minRequests(5).build());
    }
}