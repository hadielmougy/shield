package io.github.shield;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class Retry implements FilterFactory {

    /**
     * Default delay millis value. 1000 is the default value for waiting between retries.
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
     * Default timeout policy.
     */
    TimeoutPolicy DEFAULT_TIMEOUT_POLICY = new FixedDelayTimeoutPolicy(DEFAULT_DELAY_VALUE,
            DEFAULT_TIMEUNIT);
    private TimeoutPolicy timeoutPolicy = DEFAULT_TIMEOUT_POLICY;
    private long delay = DEFAULT_DELAY_VALUE;
    private TimeUnit timeUnit = DEFAULT_TIMEUNIT;
    private int maxRetries = DEFAULT_RETRIES;
    private List<Class<? extends Exception>> exceptions = new ArrayList<>();

    /**
     * Delay milliseconds between retries.
     *
     * @param delay value of milliseconds
     * @return Retry config builder
     */
    Retry delayMillis(long delay) {
        final String err = "delay must be positive value";
        Validations.checkArgument(delay > 0, err);
        this.delay = delay;
        this.timeUnit = TimeUnit.MILLISECONDS;
        this.timeoutPolicy.setDelay(delay);
        this.timeoutPolicy.setTimeunit(this.timeUnit);
        return this;
    }

    /**
     * Delay seconds between retries.
     *
     * @param delay
     * @return Retry config builder
     */
    Retry delaySeconds(long delay) {
        final String err = "delay must be positive value";
        Validations.checkArgument(delay > 0, err);
        this.delay = delay;
        this.timeUnit = TimeUnit.SECONDS;
        this.timeoutPolicy.setDelay(delay);
        this.timeoutPolicy.setTimeunit(this.timeUnit);
        return this;
    }

    /**
     * Maximum number of retries before give up.
     *
     * @param maxRetries
     * @return Retry config builder
     */
    Retry maxRetries(int maxRetries) {
        final String err = "maxRetries must be positive value";
        Validations.checkArgument(maxRetries > 0, err);
        this.maxRetries = maxRetries;
        return this;
    }

    /**
     * Fixed delay timeout policy.
     *
     * @return Retry config builder
     */
    Retry fixed() {
        timeoutPolicy = new FixedDelayTimeoutPolicy(delay, timeUnit);
        return this;
    }

    /**
     * BackOff delay timeout policy.
     *
     * @return Retry config builder
     */
    Retry backOff() {
        timeoutPolicy = new BackOffTimeoutPolicy(delay, timeUnit);
        return this;
    }

    /**
     * Can be called many times to set exceptions that must be retries on.
     *
     * @param ex exception class
     * @return Retry config builder
     */
    Retry onException(Class<? extends Exception> ex) {
        final String err = "exception class must not be null";
        exceptions.add(Objects.requireNonNull(ex, err));
        return this;
    }

    public Filter build() {
        return new RetryFilter(maxRetries, delay, timeUnit, exceptions, timeoutPolicy);
    }
}
