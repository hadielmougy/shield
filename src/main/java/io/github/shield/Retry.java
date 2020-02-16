package io.github.shield;

import io.github.shield.internal.RetryFilter;
import io.github.shield.internal.Validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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


        private long delay = 1000;
        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        private int maxRetries = 3;
        private List<Class<? extends Exception>> exceptions = new ArrayList<>();

        @Override
        public Retry delayMillis(long delay) {
            Validations.checkArgument(delay > 0, "delay must be positive value");
            this.delay = delay;
            this.timeUnit = TimeUnit.MILLISECONDS;
            return this;
        }

        @Override
        public Retry delaySeconds(long delay) {
            Validations.checkArgument(delay > 0, "delay must be positive value");
            this.delay = delay;
            this.timeUnit = TimeUnit.SECONDS;
            return this;
        }

        @Override
        public Retry maxRetries(int maxRetries) {
            Validations.checkArgument(maxRetries > 0, "maxRetries must be positive value");
            this.maxRetries = maxRetries;
            return this;
        }

        @Override
        public Retry onException(Class<? extends Exception> ex) {
            exceptions.add(Objects.requireNonNull(ex, "exception class must not be null"));
            return this;
        }

        @Override
        public Filter build() {
            return new RetryFilter(maxRetries, delay, timeUnit, exceptions);
        }
    }
}
