package io.github.shield;


public class TestComponentWithFallback implements Component {

  private final Callback callback;
  private final Fallback fallback;

  public TestComponentWithFallback(Callback callback, Fallback fallback) {
    this.callback = callback;
    this.fallback = fallback;
  }

  @Override
  public void doCall() {
    callback.doCall();
  }

  @Override
  public void doCallFallback() {
    fallback.doCallFallback();
  }
}
