package io.github.shield;

import io.github.shield.internal.DirectConnector;

/**
 *
 */
public interface DirectCall extends ConnectorFactory {


    /**
     *
     */
    class Config extends BaseConfig implements DirectCall {
        @Override
        public Connector connector() {
            return new DirectConnector(getObject());
        }
    }


}
