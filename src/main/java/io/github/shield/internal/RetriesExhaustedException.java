package io.github.shield.internal;

public class RetriesExhaustedException extends RuntimeException {

    public RetriesExhaustedException(Throwable cause) {
        super(cause);
    }
}
