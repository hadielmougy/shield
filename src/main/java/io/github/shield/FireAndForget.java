package io.github.shield;

public class FireAndForget implements FilterFactory {

  public Filter build() {
    return new FireAndForgetFilter();
  }

}
