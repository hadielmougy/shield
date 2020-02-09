package io.github.shield.internal;

import io.github.shield.Connector;
import io.github.shield.InvocationContext;
import io.github.shield.util.ClassUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 */
public class InvocationInterceptor implements MethodInterceptor {


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


    /**
     *
     * @param obj
     * @param method
     * @param args
     * @param proxy
     * @return
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
        return connector.invoke(() -> {
            try {
                return proxy.invokeSuper(obj, args);
            } catch (InvocationNotPermittedException th) {
                return callFallbackIfFound(method.getName(), obj, args);
            } catch (Throwable th) {
                th.printStackTrace();
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
