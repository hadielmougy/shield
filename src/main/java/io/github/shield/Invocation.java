package io.github.shield;


import io.github.shield.internal.InvocationContext;

public interface Invocation {

    Object invoke(InvocationContext context);
}
