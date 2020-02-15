package io.github.shield.internal;

import io.github.shield.InvocationException;
import io.github.shield.Invoker;

public class TargetMethodInvoker implements Invoker {




    @Override
    public Object invoke(InvocationContext context) {

        try {
            return context.invoke();
        } catch (InvocationNotPermittedException ex) {
            throw ex;
        } catch (InvocationException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
