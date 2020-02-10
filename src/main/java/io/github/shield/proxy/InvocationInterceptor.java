package io.github.shield.proxy;

import io.github.shield.Connector;
import io.github.shield.internal.InvocationNotPermittedException;
import io.github.shield.util.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 */
public class InvocationInterceptor implements java.lang.reflect.InvocationHandler {


    /**
     *
     */
    private final Connector connector;
    private static Logger LOG = Logger.getLogger(InvocationInterceptor.class.getName());


    /**
     *
     * @param connector
     */
    public InvocationInterceptor(Connector connector) {
        this.connector = connector;
    }




    @Override
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
        return connector.invoke(() -> {
            try {
                if (connector.getTargetComponent() == null) {
                    return null;
                } else {
                    return method.invoke(connector.getTargetComponent(), args);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Throwable th = e.getCause();
                if (th instanceof InvocationNotPermittedException) {
                    return callFallbackIfFound(method.getName(), obj, args);
                } else {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    /**
     *
     * @param methodName
     * @param target
     * @param args
     * @return
     */
    private Object callFallbackIfFound(String methodName, Object target, Object[] args) {
        String fallbackName = methodName + "Fallback";
        Method fallback = null;
        Object result = null;
        try {
            fallback = target.getClass().getDeclaredMethod(fallbackName, ClassUtil.toClassArray(args));
        } catch (NoSuchMethodException e) {
            LOG.info(String.format("No fallback is found for %s", methodName));
        }
        try {
            if (fallback != null) {
                result = fallback.invoke(target, args);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LOG.log(Level.SEVERE, String.format("Error invoking fallback method for for %s", methodName));
        }
        return result;
    }
}
