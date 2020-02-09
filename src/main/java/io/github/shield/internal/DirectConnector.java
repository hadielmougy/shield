package io.github.shield.internal;

import io.github.shield.Connector;

import java.util.function.Supplier;

/**
 *
 */
public class DirectConnector extends Connector {


    @Override
    public boolean beforeInvocation() {
        return true;
    }

    @Override
    public void afterInvocation() {
        // do nothting
    }
}
