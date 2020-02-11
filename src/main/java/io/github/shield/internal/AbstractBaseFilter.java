package io.github.shield.internal;

import io.github.shield.Filter;


public abstract class AbstractBaseFilter implements Filter {

    @Override
    public int compareTo(Filter o) {
        return this.getOrder().compareTo(o.getOrder());
    }

    @Override
    public Integer getOrder() {
        return 0;
    }
}
