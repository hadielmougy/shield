package io.github.shield;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ConcurrentLimiterTest {

    private ExecutorService executor;

    @Before
    public void init() {
        executor = Executors.newFixedThreadPool(4);
    }

    @Test
    public void testThrottled() throws InterruptedException {

        Shield shield = new Shield();
        shield.addFilter(Filter.throttler()
                .ofMax(1)
                .ofMaxWaitMillis(500)
                .build());


        final AtomicInteger counter               = new AtomicInteger(0);

        TestComponentWithFallback targetObj
                = new TestComponentWithFallback(() -> {
            counter.incrementAndGet();
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, () -> counter.decrementAndGet());

        final Component comp = shield.forObject(targetObj).as(Component.class);

        executor.submit(() -> comp.doCall());
        executor.submit(() -> comp.doCall());

        TimeUnit.MILLISECONDS.sleep(100);

        Assert.assertEquals(1, counter.get());

        executor.shutdown();

    }


    @Test
    public void testDefault() {
        Shield shield = new Shield();
        shield.addFilter(Filter.directCall().build());

        final AtomicInteger counter               = new AtomicInteger(0);

        TestComponentWithFallback targetObj
                = new TestComponentWithFallback(() -> {
            counter.incrementAndGet();
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, () -> counter.decrementAndGet());

        final Component comp = shield.forObject(targetObj).as(Component.class);

        executor.submit(() -> comp.doCall());
        executor.submit(() -> comp.doCall());

        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> counter.get() == 2);

        Assert.assertEquals(2, counter.get());

        executor.shutdown();

    }

}
