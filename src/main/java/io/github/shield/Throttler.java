package io.github.shield;


import io.github.shield.internal.ThrottlingFilter;
import io.github.shield.internal.Validations;


/**
 *
 */
public interface Throttler extends FilterFactory {

    /**
     *
     * @param max
     * @return
     */
    Throttler requests(int max);


    /**
     *
     * @param maxWait
     * @return
     */
    Throttler maxWaitMillis(long maxWait);


    /**
     *
     */
    class Config implements Throttler {

        private int max = 10;
        private long wait = 500;

        @Override
        public Throttler requests(int max) {
            Validations.checkArgument(max > 0, "Max requests must be positive");
            this.max = max;
            return this;
        }

        @Override
        public Throttler maxWaitMillis(long maxWait) {
            Validations.checkArgument(wait > 0, "wait value must be positive");
            this.wait = maxWait;
            return this;
        }


        @Override
        public Filter build() {
            return new ThrottlingFilter(max, wait);
        }
    }
}
