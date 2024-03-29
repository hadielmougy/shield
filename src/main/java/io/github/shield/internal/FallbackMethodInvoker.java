package io.github.shield.internal;

import io.github.shield.InvocationException;
import io.github.shield.Invoker;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class FallbackMethodInvoker implements Invoker {


  /**
   * {@inheritDoc}
   */
  @Override
  public Object invoke(final InvocationContext context) {
    try {
      return context.executeFallback();
    } catch (InvocationException e) {
      System.err.printf("Error invoking fallback method with cause %s", e.getMessage());
    }
    return null;
  }


}
