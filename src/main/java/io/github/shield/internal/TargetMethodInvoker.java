package io.github.shield.internal;

import io.github.shield.Invoker;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class TargetMethodInvoker<T> implements Invoker<T> {

  @Override
  public T invoke(InvocationContext<T> context) {
    return context.get();
  }

}
