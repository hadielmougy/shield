package io.github.shield;

import io.github.shield.internal.DirectConnector;

public interface DirectCall extends ShieldFactory {



    class Config implements DirectCall {
        @Override
        public Connector connector() {
            return new DirectConnector();
        }
    }


}
