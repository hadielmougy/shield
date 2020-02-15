package io.github.shield.internal;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RetryFilter extends AbstractBaseFilter {

    private final long delay;
    private final TimeUnit timeUnit;
    private final int retries;
    private final List<Class<? extends Exception>> exceptions;
    private boolean retryOnAll;


    public RetryFilter(int retries, long delay, TimeUnit timeUnit, List<Class<? extends Exception>> exceptions) {
        this.retries = retries;
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.exceptions = exceptions;

        if (exceptions.isEmpty()) {
            retryOnAll = true;
        }
    }


    @Override
    public boolean beforeInvocation() {
        return true;
    }


    @Override
    public void afterInvocation() {
    }



    @Override
    public Object invoke() {
        Object result = null;
        int counter = 1;
        Throwable th = null;
        while(counter <= retries) {
            th = null;
            try {
                result = invokeNext();
                break;
            } catch (Throwable th_) {
                th = th_;
                if (!shouldRetry(th_)) {
                    break;
                }
            }
            counter++;
        }

        if (th != null) {
            throw new RetriesExhaustedException(th);
        }
        return result;
    }



    private boolean shouldRetry(Throwable th) {
        if (retryOnAll) {
            return true;
        }

        for (Class clazz : exceptions) {
            if (th.getClass().equals(clazz) ||
                    searchStaskTrace(th, clazz, 2)) {
                return true;
            }
        }

        return false;
    }


    private boolean searchStaskTrace(Throwable throwable, Class clazz, int depth) {
        int counter = 0;
        Throwable th = throwable.getCause();
        while (th != null && counter < depth) {
            Class thClazz = th.getClass();
            if (thClazz.equals(clazz)) {
                return true;
            }
            th = th.getCause();
            counter++;
        }
        return false;
    }
}