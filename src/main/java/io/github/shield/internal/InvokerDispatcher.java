package io.github.shield.internal;

import io.github.shield.Invoker;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class InvokerDispatcher<T> implements Invoker<T> {

  private final TargetMethodInvoker<T> targetMethodInvoker;


  public InvokerDispatcher(TargetMethodInvoker<T> targetMethodInvoker) {
    this.targetMethodInvoker = targetMethodInvoker;
  }


  @Override
  public T invoke(final InvocationContext<T> context) {
    return targetMethodInvoker.invoke(context);
  }

}
