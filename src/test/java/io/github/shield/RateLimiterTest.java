package io.github.shield;

import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterTest {

    private ExecutorService executor;

    @Before
    public void init() {
        executor = Executors.newFixedThreadPool(4);
    }

    @Test
    public void testLimited1() throws InterruptedException {
        Shield shield = new Shield();
        shield.addFilter(Filter.rateLimiter()
                .withRate(1).build());

        final AtomicInteger counter               = new AtomicInteger(0);

        Component component = new TestComponentWithFallback(() -> {
            counter.incrementAndGet();
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, ()-> counter.decrementAndGet());

        final Component comp = shield
                .forObject(component)
                .as(Component.class);


        executor.submit(() -> comp.doCall());
        executor.submit(() -> comp.doCall());

        TimeUnit.MILLISECONDS.sleep(100);

        Assert.assertEquals(1, counter.get());

        executor.shutdown();

    }


    @Test
    public void testLimited2() {
        Shield shield = new Shield();
        shield.addFilter(Filter.rateLimiter()
                .withRate(2).build());

        final AtomicInteger counter               = new AtomicInteger(0);

        Component component = new TestComponentWithFallback(() -> {
            counter.incrementAndGet();
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, ()-> counter.decrementAndGet());

        final Component comp = shield.forObject(component).as(Component.class);

        executor.submit(() -> comp.doCall());
        executor.submit(() -> comp.doCall());

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(()-> counter.get() == 2);

        Assert.assertEquals(2, counter.get());
        executor.shutdown();
    }



}
