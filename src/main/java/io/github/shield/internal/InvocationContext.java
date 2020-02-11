package io.github.shield.internal;

import io.github.shield.Filter;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class InvocationContext {

    private Supplier supplier;

    private final List<Filter> filters;
    private final Object targetObject;
    private final Method targetMethod;
    private final Object[] args;
    private final MethodProxy proxy;


    public InvocationContext(List<Filter> filters,
                             Object targetObject,
                             Method targetMethod,
                             Object[] args,
                             MethodProxy proxy) {
        this.filters = filters;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.args = args;
        this.proxy = proxy;

        initSupplier();
    }

    private void initSupplier() {
        this.supplier = () -> {
            try {
                return proxy.invokeSuper(targetObject, args);
            } catch (InvocationNotPermittedException ex) {
                throw ex;
            } catch (Throwable th) {
                th.printStackTrace();
            }
            return null;
        };
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }


    public List<Filter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getArgs() {
        return args;
    }

    public MethodProxy getProxy() {
        return proxy;
    }
}
