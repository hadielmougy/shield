package io.github.shield.internal;

import io.github.shield.Connector;
import io.github.shield.InvocationContext;

/**
 *
 */
public class DirectConnector extends Connector {


    public DirectConnector(Object target) {
        super(target);
    }

    @Override
    public boolean beforeInvocation(InvocationContext context) {
        return true;
    }

    @Override
    public void afterInvocation(InvocationContext context) {
        // do nothting
    }
}
