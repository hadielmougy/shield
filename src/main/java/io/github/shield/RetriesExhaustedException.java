package io.github.shield;

public class RetriesExhaustedException extends RuntimeException {

    public RetriesExhaustedException(Throwable cause) {
        super(cause);
    }
}
