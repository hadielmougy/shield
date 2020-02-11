package io.github.shield.internal;


/**
 *
 */
public class DirectFilter extends AbstractBaseFilter {


    @Override
    public boolean beforeInvocation(InvocationContext context) {
        return true;
    }

    @Override
    public void afterInvocation(InvocationContext context) {
        // do nothting
    }
}
