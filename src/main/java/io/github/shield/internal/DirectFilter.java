package io.github.shield.internal;

import io.github.shield.Filter;

/**
 *
 */
public class DirectFilter implements Filter {


    @Override
    public boolean beforeInvocation(InvocationContext context) {
        return true;
    }

    @Override
    public void afterInvocation(InvocationContext context) {
        // do nothting
    }
}
