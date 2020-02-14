package io.github.shield.internal;

/**
 * filter chain to move invocation to the filter instead of context
 */
public class InvocationChain {

    private AbstractBaseFilter root;

    public InvocationChain(AbstractBaseFilter root) {
        this.root = root;
    }



}
