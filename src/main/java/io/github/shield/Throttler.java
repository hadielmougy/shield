package io.github.shield;


public class Throttler implements FilterFactory {

    /**
     * default maximum requests.
     */
    private int max = 10;
    /**
     * default wait millis.
     */
    private long wait = 500;

    /**
     * Set the maximum number of concurrent requests.
     *
     * @param max positive number of requests default (10)
     * @return Throttle config builder
     */
    Throttler requests(int max) {
        Validations.checkArgument(max > 0, "Max requests must be positive");
        this.max = max;
        return this;
    }


    /**
     * Maximum milliseconds to wait before give up the current thread to continue if suspended due to
     * maximum requests are already running.
     *
     * @param maxWait wait milliseconds
     * @return Throttle config builder
     */
    Throttler maxWaitMillis(long maxWait) {
        Validations.checkArgument(wait > 0, "wait value must be positive");
        this.wait = maxWait;
        return this;
    }

    @Override
    public Filter build() {
        return new ThrottlingFilter(max, wait);
    }

}
