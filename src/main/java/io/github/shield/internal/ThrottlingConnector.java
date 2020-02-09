package io.github.shield.internal;

import java.util.function.Supplier;


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




    /**
     *
     * @param supplier
     * @return
     */
    @Override
    public Object invoke(Supplier supplier) {
        Object result = null;
        try {
            result = super.invoke(supplier);
        } finally {
            release();
        }
        return result;
    }
}
