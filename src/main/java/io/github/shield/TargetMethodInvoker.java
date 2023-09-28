package io.github.shield;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class TargetMethodInvoker implements Invoker {

    @Override
    public Object invoke(InvocationContext context) {
        return context.execute();
    }

}
