package io.github.shield;


import io.github.shield.internal.InvocationContext;

public interface Invoker<T> {

  T invoke(InvocationContext<T> context);

}
