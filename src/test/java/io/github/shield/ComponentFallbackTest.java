package io.github.shield;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ComponentFallbackTest {

    private ExecutorService executor;

    @Before
    public void init() {
        executor = Executors.newFixedThreadPool(4);
    }

    @Test
    public void testThrottledAndFallback() {
        final IDefaultComponent comp = Connector.throttler()
                .ofMax(1)
                .ofMaxWaitMillis(500)
                .forObject(new DefaultComponent())
                .as(IDefaultComponent.class);

        final IForwarderComponent forwarder = Connector
                .directCall()
                .forObject(new ForwarderComponent(comp))
                .as(IForwarderComponent.class);


        final AtomicInteger callCounter             = new AtomicInteger(0);
        final AtomicInteger fallBackCounter         = new AtomicInteger(0);

        executor.submit(() -> forwarder.doCall(callCounter, fallBackCounter));
        executor.submit(() -> forwarder.doCall(callCounter, fallBackCounter));

        Awaitility.await().until(() -> fallBackCounter.get() == 1);

        Assert.assertEquals(1, callCounter.get());
        Assert.assertEquals(1, fallBackCounter.get());

        executor.shutdown();

    }


    public static interface IForwarderComponent {

        void doCall(AtomicInteger counter, AtomicInteger fallback);


        void doCallFallback(AtomicInteger counter, AtomicInteger fallback);

    }

    public static class ForwarderComponent implements IForwarderComponent {

        private final IDefaultComponent component;

        public ForwarderComponent(IDefaultComponent component) {
            this.component = component;
        }


        public void doCall(AtomicInteger counter, AtomicInteger fallback) {
            component.doCall(counter, fallback);
        }


        public void doCallFallback(AtomicInteger counter, AtomicInteger fallback) {
            fallback.incrementAndGet();
        }

    }


    public static interface IDefaultComponent {

        void doCall(AtomicInteger counter, AtomicInteger fallback);

    }


    public static class DefaultComponent implements IDefaultComponent {

        public void doCall(AtomicInteger counter, AtomicInteger fallback) {
            try {
                counter.incrementAndGet();
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
