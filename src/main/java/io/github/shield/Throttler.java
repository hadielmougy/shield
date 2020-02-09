package io.github.shield;


import io.github.shield.internal.ThrottlingConnector;
import io.github.shield.internal.Validations;


/**
 *
 */
public interface Throttler extends ConnectorFactory {

    /**
     *
     * @param max
     * @return
     */
    Throttler ofMax(int max);


    /**
     *
     * @param maxWait
     * @return
     */
    Throttler ofMaxWaitMillis(long maxWait);


    /**
     *
     */
    class Config implements Throttler {

        int max;
        long wait;

        @Override
        public Throttler ofMax(int max) {
            this.max = max;
            return this;
        }

        @Override
        public Throttler ofMaxWaitMillis(long maxWait) {
            this.wait = maxWait;
            return this;
        }


        @Override
        public Connector connector() {
            Validations.checkArgument(max > 0, "Max requests must be positive");
            Validations.checkArgument(wait > 0, "wait value must be positive");
            return new ThrottlingConnector(max, wait);
        }
    }
}
