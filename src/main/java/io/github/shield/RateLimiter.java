package io.github.shield;

import io.github.shield.internal.RateLimiterFilter;
import io.github.shield.internal.Validations;


/**
 *
 */
public interface RateLimiter extends FilterFactory {

    /**
     *
     * @param rate
     * @return
     */
    RateLimiter withRate(int rate);


    /**
     *
     */
    class Config implements RateLimiter {

        int rate;

        @Override
        public RateLimiter withRate(int rate) {
            this.rate = rate;
            return this;
        }

        @Override
        public Filter build() {
            Validations.checkArgument( rate > 0, "rate must be positive");
            return new RateLimiterFilter(rate);
        }
    }
}
