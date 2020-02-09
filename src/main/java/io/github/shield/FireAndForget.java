package io.github.shield;

import io.github.shield.internal.FireAndForgetConnector;

public interface FireAndForget extends ConnectorFactory {


    /**
     *
     */
    class Config extends BaseConfig implements FireAndForget {
        @Override
        public Connector connector() {
            return new FireAndForgetConnector(getObject());
        }
    }

}
