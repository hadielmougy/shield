package io.github.shield;


/**
 * Thrown when a target invocation does not complete within the configured timeout.
 */
public class TimeoutExceededException extends RuntimeException {

  public TimeoutExceededException(String msg) {
    super(msg);
  }
}