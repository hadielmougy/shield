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
    RateLimiter rate(int rate);


    /**
     *
     */
    class Config implements RateLimiter {

        private int rate = 10;

        @Override
        public RateLimiter rate(int rate) {
            Validations.checkArgument(rate > 0, "rate must be positive");
            this.rate = rate;
            return this;
        }

        @Override
        public Filter build() {
            return new RateLimiterFilter(rate);
        }
    }
}
