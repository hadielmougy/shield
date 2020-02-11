package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.InvocationContext;

/**
 *
 */
public class DirectFilter extends Filter {


    @Override
    public boolean beforeInvocation(InvocationContext context) {
        return true;
    }

    @Override
    public void afterInvocation(InvocationContext context) {
        // do nothting
    }
}
