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

        final SingleThreadedDefaultComponent comp = shield
                .as(SingleThreadedDefaultComponent.class);

        final AtomicInteger counter               = new AtomicInteger(0);

        executor.submit(() -> comp.doCall(counter));
        executor.submit(() -> comp.doCall(counter));

        TimeUnit.MILLISECONDS.sleep(100);

        Assert.assertEquals(1, counter.get());

        executor.shutdown();

    }


    @Test
    public void testDefault() {
        Shield shield = new Shield();
        shield.addFilter(Filter.directCall().build());
        final SingleThreadedDefaultComponent comp = shield.as(SingleThreadedDefaultComponent.class);

        final AtomicInteger counter               = new AtomicInteger(0);

        executor.submit(() -> comp.doCall(counter));
        executor.submit(() -> comp.doCall(counter));

        Awaitility.await().atMost(1, TimeUnit.SECONDS).until(() -> counter.get() == 2);

        Assert.assertEquals(2, counter.get());

        executor.shutdown();

    }



    public static class SingleThreadedDefaultComponent {

        public void doCall(AtomicInteger counter) {
            try {
                counter.incrementAndGet();
                TimeUnit.SECONDS.sleep(5);
                counter.decrementAndGet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
