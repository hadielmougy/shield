package io.github.shield.internal;


/**
 *
 */
public class InvocationNotPermittedException extends RuntimeException {
    private final Class<?> throwingClass;

    public InvocationNotPermittedException(Class<?> throwingClass) {
        super();
        this.throwingClass = throwingClass;
    }


    public Class<?> getThrowingClass() {
        return throwingClass;
    }
}
