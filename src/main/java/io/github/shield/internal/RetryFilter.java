package io.github.shield.internal;

import io.github.shield.Filter;

public class RetryFilter implements Filter {
    @Override
    public boolean beforeInvocation(InvocationContext context) {
        //TODO
        return true;
    }

    @Override
    public void afterInvocation(InvocationContext context) {
        // TODO
    }
}
