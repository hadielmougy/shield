package io.github.shield;

import io.github.shield.internal.AbstractBaseInterceptor;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Verifies that every interceptor in a chain of three or more is invoked, in order. This guards
 * the {@code SupplierWrapper.reduceFilters} chaining logic.
 */
public class InterceptorChainTest {

    /** Records its label before delegating to the next interceptor in the chain. */
    private static class RecordingInterceptor extends AbstractBaseInterceptor {
        private final List<String> log;
        private final String label;
        private final int order;

        RecordingInterceptor(List<String> log, String label, int order) {
            this.log = log;
            this.label = label;
            this.order = order;
        }

        @Override
        public Integer getOrder() {
            return order;
        }

        @Override
        public boolean beforeInvocation() {
            return true;
        }

        @Override
        public void afterInvocation() {
            // no-op
        }

        @Override
        public Object invoke(Supplier supplier) {
            log.add(label);
            return invokeNext(supplier);
        }
    }

    @Test
    public void allInterceptorsInAChainAreInvokedInOrder() {
        final List<String> log = new CopyOnWriteArrayList<>();
        final Supplier<String> target = () -> {
            log.add("target");
            return "ok";
        };

        Supplier<String> decorated = Shield.decorate(target)
                .with(() -> new RecordingInterceptor(log, "A", 0))
                .with(() -> new RecordingInterceptor(log, "B", 1))
                .with(() -> new RecordingInterceptor(log, "C", 2))
                .build();

        String result = decorated.get();

        Assert.assertEquals("ok", result);
        Assert.assertEquals(List.of("A", "B", "C", "target"), log);
    }

    @Test
    public void singleInterceptorChainStillReachesTarget() {
        final List<String> log = new CopyOnWriteArrayList<>();
        final Supplier<String> target = () -> {
            log.add("target");
            return "ok";
        };

        Supplier<String> decorated = Shield.decorate(target)
                .with(() -> new RecordingInterceptor(log, "A", 0))
                .build();

        Assert.assertEquals("ok", decorated.get());
        Assert.assertEquals(List.of("A", "target"), log);
    }
}
