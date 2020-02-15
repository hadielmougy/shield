package io.github.shield;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FireAndForgetConnectorTest {


    private ExecutorService executor;

    @Before
    public void init() {
        executor = Executors.newFixedThreadPool(4);
    }

    @Test
    public void testRunningInDifferentThread() throws InterruptedException {

        Shield shield = new Shield();
        shield.addFilter(Filter.fireAndForget().build());

        final StringBuilder stringBuilder = new StringBuilder();

        TestComponentWithFallback targetObj
                = new TestComponentWithFallback(() -> stringBuilder.append(Thread.currentThread().getName()), null);

        final Component comp = shield.forObject(targetObj).as(Component.class);


        executor.submit(() -> comp.doCall());

        TimeUnit.MILLISECONDS.sleep(100);
        String currentThreadName = Thread.currentThread().getName();
        Assert.assertNotEquals(currentThreadName, stringBuilder.toString());
        executor.shutdown();

    }


}
