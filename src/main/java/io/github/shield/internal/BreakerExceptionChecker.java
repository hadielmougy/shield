package io.github.shield.internal;

import io.github.shield.util.ExceptionUtil;

import java.util.Arrays;

public final class BreakerExceptionChecker {
    private final Class<? extends Throwable>[] recordExceptions;
    private final Class<? extends Throwable>[] ignoreExceptions;

    public BreakerExceptionChecker(Class<? extends Throwable>[] ignoreExceptions,
                                   Class<? extends Throwable>[] recordExceptions) {
        this.ignoreExceptions = ignoreExceptions;
        this.recordExceptions = recordExceptions;
    }

    public boolean shouldRecord(Throwable th) {
        if (recordExceptions.length == 0) {
            boolean shouldIgnore = Arrays.stream(ignoreExceptions)
                    .anyMatch(clazz -> ExceptionUtil.isClassFoundInStackTrace(th, clazz, 2));
            return !shouldIgnore;
        } else {
            for (Class<? extends Throwable> clazz : recordExceptions) {
                if (ExceptionUtil.isClassFoundInStackTrace(th, clazz, 2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
