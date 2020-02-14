package io.github.shield;


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
