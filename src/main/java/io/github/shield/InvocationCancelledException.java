package io.github.shield;


/**
 *
 */
public class InvocationCancelledException extends RuntimeException {



  public InvocationCancelledException(String msg) {
    super(msg);
  }

  @Override
  public String toString() {
    return "InvocationCancelledException{" +
            "msg='" + getMessage() + '\'' +
            '}';
  }
}
