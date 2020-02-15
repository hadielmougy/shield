package io.github.shield.internal;


import java.util.function.Supplier;

/**
 *
 */
public class DirectInvocationFilter extends AbstractBaseFilter {


    private final Supplier valueSupplier;

    public DirectInvocationFilter(Supplier supplier) {
        this.valueSupplier = supplier;
    }


    @Override
    public boolean beforeInvocation() {
        return true;
    }

    @Override
    public void afterInvocation() {
        // do nothting
    }

    @Override
    public Object invoke() {
        return valueSupplier.get();
    }
}
