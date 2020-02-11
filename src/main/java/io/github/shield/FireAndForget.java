package io.github.shield;

import io.github.shield.internal.FireAndForgetFilter;

public interface FireAndForget extends FilterFactory {


    /**
     *
     */
    class Config implements FireAndForget {
        @Override
        public Filter build() {
            return new FireAndForgetFilter();
        }
    }

}
