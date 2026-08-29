package io.github.shield;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.Supplier;

public class TimeoutTest {

    @Test
    public void returnsResultWhenCallCompletesInTime() {
        Supplier<String> target = () -> "ok";
        Supplier<String> decorated = Shield.decorate(target)
                .with(Interceptor.timeout().waitMillis(1000))
                .build();

        Assert.assertEquals("ok", decorated.get());
    }

    @Test
    public void throwsTimeoutWhenCallExceedsDeadline() {
        Supplier<String> slow = () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "too late";
        };
        Supplier<String> decorated = Shield.decorate(slow)
                .with(Interceptor.timeout().waitMillis(200))
                .build();

        Assert.assertThrows(TimeoutExceededException.class, decorated::get);
    }

    @Test
    public void propagatesTargetExceptionInsteadOfSwallowingIt() {
        Supplier<String> failing = () -> {
            throw new IllegalStateException("boom");
        };
        Supplier<String> decorated = Shield.decorate(failing)
                .with(Interceptor.timeout().waitMillis(1000))
                .build();

        IllegalStateException thrown =
                Assert.assertThrows(IllegalStateException.class, decorated::get);
        Assert.assertEquals("boom", thrown.getMessage());
    }
}
