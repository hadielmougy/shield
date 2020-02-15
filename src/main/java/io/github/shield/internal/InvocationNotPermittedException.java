package io.github.shield.internal;


import io.github.shield.InvocationException;

/**
 *
 */
public class InvocationNotPermittedException extends InvocationException {

    private final Class<?> throwingClass;

    public InvocationNotPermittedException(Class<?> throwingClass) {
        super(null);
        this.throwingClass = throwingClass;
    }


    public Class<?> getThrowingClass() {
        return throwingClass;
    }
}
