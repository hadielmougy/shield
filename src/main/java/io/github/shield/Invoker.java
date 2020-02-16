package io.github.shield;


import io.github.shield.internal.InvocationContext;

public interface Invoker {

    Object invoke(InvocationContext context);

}
