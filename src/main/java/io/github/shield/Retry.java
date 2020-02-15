package io.github.shield;

import io.github.shield.internal.RetryFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public interface Retry extends FilterFactory {

    Retry withDelay(long delay);

    Retry withMaxRetries(int maxRetries);

    Retry timeUnit(TimeUnit timeUnit);

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
        public Retry withDelay(long delay) {
            this.delay = delay;
            return this;
        }

        @Override
        public Retry withMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        @Override
        public Retry timeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
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
