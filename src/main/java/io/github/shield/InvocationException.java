package io.github.shield;

public class InvocationException extends RuntimeException {


  /**
   * Constructor.
   *
   * @param cause cause throwable
   */
  public InvocationException(final Throwable cause) {
    super(cause);
  }
}
