package io.github.shield.util;

public class ExceptionUtil {

    public static boolean isClassFoundInStackTrace(Throwable throwable, Class<?> clazz, int depth) {
        int counter = 0;
        Throwable th = throwable.getCause();
        while (th != null && counter < depth) {
            Class<?> thClazz = th.getClass();
            if (thClazz.equals(clazz) || clazz.isAssignableFrom(thClazz)) {
                return true;
            }
            th = th.getCause();
            counter++;
        }
        return false;
    }
}
