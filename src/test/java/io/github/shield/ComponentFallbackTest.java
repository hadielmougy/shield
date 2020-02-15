package io.github.shield;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ComponentFallbackTest {

    private ExecutorService executor;

    @Before
    public void init() {
        executor = Executors.newFixedThreadPool(4);
    }

    @Test(expected = InvocationNotPermittedException.class)
    public void testThrottledAndFallback() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);

        Shield throttlerShield = new Shield();

        throttlerShield.addFilter(Filter.throttler()
                .ofMax(1)
                .ofMaxWaitMillis(500)
                .build());

        TestComponentWithFallback targetObj
                = new TestComponentWithFallback(() -> {
                    counter.incrementAndGet();
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, () -> counter.decrementAndGet());

        final Component comp = throttlerShield
                .forObject(targetObj)
                .as(Component.class);


        executor.submit(() -> comp.doCall());

        Thread.currentThread().sleep(500);

        comp.doCall();

        Awaitility.await().until(() -> counter.get() == 1);
        Assert.assertEquals(1, counter.get());

        executor.shutdown();
    }

}
