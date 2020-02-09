package io.github.shield;

import io.github.shield.proxy.JdkProxyFactory;


/**
 *
 */
public interface ConnectorFactory {


    /**
     *
     * @param type
     * @param <T>
     * @return
     */
    default  <T> T as(Class<T> type) {
        return JdkProxyFactory.proxy(type, connector());
    }

    ConnectorFactory forObject(Object obj);


    /**
     *
     * @return
     */
    Connector connector();


    abstract class BaseConfig implements ConnectorFactory {

        private Object object;

        @Override
        public ConnectorFactory forObject(Object obj) {
            this.object = obj;
            return this;
        }

        protected Object getObject() {
            return object;
        }
    }
}
