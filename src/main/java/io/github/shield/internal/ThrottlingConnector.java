package io.github.shield.internal;



/**
 *
 */
public class ThrottlingConnector extends AbstractLimiterBase {



    /**
     *
     * @param max
     * @param maxWaitMillis
     */
    public ThrottlingConnector(int max, long maxWaitMillis) {
        super(max, maxWaitMillis);
    }




    @Override
    public void afterInvocation() {
        release();
    }
}
