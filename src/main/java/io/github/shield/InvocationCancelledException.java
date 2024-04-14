package io.github.shield;


/**
 *
 */
public class InvocationCancelledException extends InvocationException {


  private final String msg;

  public InvocationCancelledException(String msg) {
    super(null);
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "InvocationCancelledException{" +
            "msg='" + msg + '\'' +
            '}';
  }
}
