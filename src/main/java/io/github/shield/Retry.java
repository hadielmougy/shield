package io.github.shield;

import io.github.shield.internal.RetryFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public interface Retry extends FilterFactory {

    Retry delayMillis(long delay);

    Retry delaySeconds(long delay);

    Retry maxRetries(int maxRetries);

    Retry onException(Class<? extends Exception> ex);


    /**
     *
     */
    class Config implements Retry {


        private long delay;
        private int maxRetries;
        private TimeUnit timeUnit;
        private List<Class<? extends Exception>> exceptions = new ArrayList<>();

        @Override
        public Retry delayMillis(long delay) {
            this.delay = delay;
            this.timeUnit = TimeUnit.MILLISECONDS;
            return this;
        }

        @Override
        public Retry delaySeconds(long delay) {
            this.delay = delay;
            this.timeUnit = TimeUnit.SECONDS;
            return this;
        }

        @Override
        public Retry maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        @Override
        public Retry onException(Class<? extends Exception> ex) {
            exceptions.add(ex);
            return this;
        }

        @Override
        public Filter build() {
            return new RetryFilter(maxRetries, delay, timeUnit, exceptions);
        }
    }
}
