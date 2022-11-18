package io.github.shield.internal;

public class Validations {

  public static void checkArgument(boolean expression, String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }
}
