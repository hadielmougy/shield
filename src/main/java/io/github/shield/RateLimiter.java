package io.github.shield;

import com.google.common.base.Preconditions;
import io.github.shield.internal.RateLimiterConnector;

public interface RateLimiter extends ShieldFactory {

    RateLimiter withRate(Double rate);


    class Config implements RateLimiter {

        double rate;

        @Override
        public RateLimiter withRate(Double rate) {
            this.rate = rate;
            return this;
        }

        @Override
        public Connector connector() {
            Preconditions.checkArgument( rate > 0, "rate must be positive");
            return new RateLimiterConnector(rate);
        }
    }
}
