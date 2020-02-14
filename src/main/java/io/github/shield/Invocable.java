package io.github.shield;

public interface Invocable {

    Object invoke() throws InvocationNotPermittedException, InvocationException;

}
