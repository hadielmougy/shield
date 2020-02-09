package io.github.shield;

import io.github.shield.internal.RateLimiterConnector;
import io.github.shield.internal.Validations;


/**
 *
 */
public interface RateLimiter extends ShieldFactory {

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
        public Connector connector() {
            Validations.checkArgument( rate > 0, "rate must be positive");
            return new RateLimiterConnector(rate);
        }
    }
}
