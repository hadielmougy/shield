package io.github.shield.internal;


import io.github.shield.InvocationContext;

/**
 *
 */
public class ThrottlingConnector extends AbstractLimiterBase {



    /**
     *  @param max
     * @param maxWaitMillis
     */
    public ThrottlingConnector(int max, long maxWaitMillis, Object target) {
        super(max, maxWaitMillis, target);
    }




    @Override
    public void afterInvocation(InvocationContext context) {
        release();
    }
}
