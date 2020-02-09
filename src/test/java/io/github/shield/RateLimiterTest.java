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
        final ISingleThreadedDefaultComponent comp = Connector.rateLimiter()
                .withRate(1)
                .forObject(new SingleThreadedDefaultComponent())
                .as(ISingleThreadedDefaultComponent.class);

        final AtomicInteger counter               = new AtomicInteger(0);

        executor.submit(() -> comp.doCall(counter));
        executor.submit(() -> comp.doCall(counter));

        TimeUnit.MILLISECONDS.sleep(100);

        Assert.assertEquals(1, counter.get());

        executor.shutdown();

    }


    @Test
    public void testLimited2() {
        final ISingleThreadedDefaultComponent comp = Connector.rateLimiter()
                .withRate(3)
                .forObject(new SingleThreadedDefaultComponent())
                .as(ISingleThreadedDefaultComponent.class);

        final AtomicInteger counter               = new AtomicInteger(0);

        executor.submit(() -> comp.doCall(counter));
        executor.submit(() -> comp.doCall(counter));

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(()-> counter.get() == 2);

        Assert.assertEquals(2, counter.get());
        executor.shutdown();
    }


    public static interface ISingleThreadedDefaultComponent {

        public void doCall(AtomicInteger counter);

    }

    public static class SingleThreadedDefaultComponent implements ISingleThreadedDefaultComponent{

        public void doCall(AtomicInteger counter) {
            try {
                counter.incrementAndGet();
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
