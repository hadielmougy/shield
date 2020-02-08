package io.github.shield.internal;

import io.github.shield.Connector;

import java.util.function.Supplier;

public class DirectConnector extends Connector {

    @Override
    public Object invoke(Supplier supplier) {
        return doInvoke(supplier);
    }

}
