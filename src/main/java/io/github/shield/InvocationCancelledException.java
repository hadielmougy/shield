package io.github.shield;



/**
 *
 */
public class InvocationCancelledException extends InvocationException {

    private final Class<?> throwingClass;

    public InvocationCancelledException(Class<?> throwingClass) {
        super(null);
        this.throwingClass = throwingClass;
    }


    public Class<?> getThrowingClass() {
        return throwingClass;
    }
}
