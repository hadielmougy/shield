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

        final DefaultComponent comp = Connector
                .fireAndForget()
                .as(DefaultComponent.class);

        final StringBuilder stringBuilder = new StringBuilder();

        executor.submit(() -> comp.doCall(stringBuilder));

        TimeUnit.MILLISECONDS.sleep(100);
        String currentThreadName = Thread.currentThread().getName();
        Assert.assertNotEquals(currentThreadName, stringBuilder.toString());
        executor.shutdown();

    }


    public static class DefaultComponent {

        public void doCall(StringBuilder sb) {
            sb.append(Thread.currentThread().getName());
        }

    }
}
