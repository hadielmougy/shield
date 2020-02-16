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

    /**
     * Default delay millis value. 1000 is the default value for
     * waiting between retries.
     */
    long DEFAULT_DELAY_VALUE = 1000;

    /**
     * Default value for retries before give up.
     */
    int DEFAULT_RETRIES = 3;

    /**
     * Default time unit for delay between retries.
     */
    TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;

    /**
     * Delay milliseconds between retries.
     * @param delay value of milliseconds
     * @return Retry config builder
     */
    Retry delayMillis(long delay);

    /**
     * Delay seconds between retries.
     * @param delay
     * @return Retry config builder
     */
    Retry delaySeconds(long delay);

    /**
     * Maximum number of retries before give up.
     * @param maxRetries
     * @return Retry config builder
     */
    Retry maxRetries(int maxRetries);

    /**
     * Can be called many times to set exceptions that must be retries on.
     * @param ex exception class
     * @return Retry config builder
     */
    Retry onException(Class<? extends Exception> ex);


    /**
     * Retry config builder.
     */
    class Config implements Retry {

        /**
         * @see Retry
         */
        private long delay = DEFAULT_DELAY_VALUE;
        /**
         * @see Retry
         */
        private TimeUnit timeUnit = DEFAULT_TIMEUNIT;
        /**
         * @see Retry
         */
        private int maxRetries = DEFAULT_RETRIES;
        /**
         * @see Retry
         */
        private List<Class<? extends Exception>> exceptions = new ArrayList<>();


        /**
         * {@inheritDoc}
         */
        @Override
        public Retry delayMillis(final long value) {
            final String err = "delay must be positive value";
            Validations.checkArgument(delay > 0, err);
            this.delay = value;
            this.timeUnit = TimeUnit.MILLISECONDS;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Retry delaySeconds(final long value) {
            final String err = "delay must be positive value";
            Validations.checkArgument(delay > 0, err);
            this.delay = value;
            this.timeUnit = TimeUnit.SECONDS;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Retry maxRetries(final int value) {
            final String err = "maxRetries must be positive value";
            Validations.checkArgument(maxRetries > 0, err);
            this.maxRetries = value;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Retry onException(final Class<? extends Exception> ex) {
            final String err = "exception class must not be null";
            exceptions.add(Objects.requireNonNull(ex, err));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Filter build() {
            return new RetryFilter(maxRetries, delay, timeUnit, exceptions);
        }
    }
}
