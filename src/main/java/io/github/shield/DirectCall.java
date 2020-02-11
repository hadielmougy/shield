package io.github.shield;

import io.github.shield.internal.DirectFilter;

/**
 *
 */
public interface DirectCall extends FilterFactory {


    /**
     *
     */
    class Config implements DirectCall {
        @Override
        public Filter build() {
            return new DirectFilter();
        }
    }


}
