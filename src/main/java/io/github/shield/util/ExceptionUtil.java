package io.github.shield.util;

public class ExceptionUtil {

    /**
     * Returns {@code true} if {@code clazz} matches the throwable itself or any throwable in its
     * cause chain, up to {@code depth} levels of causes.
     *
     * <p>The thrown throwable is inspected first (level 0), followed by up to {@code depth} causes.
     */
    public static boolean isClassFoundInStackTrace(Throwable throwable, Class<?> clazz, int depth) {
        int counter = 0;
        Throwable th = throwable;
        while (th != null && counter <= depth) {
            Class<?> thClazz = th.getClass();
            if (thClazz.equals(clazz) || clazz.isAssignableFrom(thClazz)) {
                return true;
            }
            th = th.getCause();
            counter++;
        }
        return false;
    }

    /**
     * Rethrows the given throwable without wrapping it, preserving its original type even when it
     * is a checked exception. The generic return type lets callers write
     * {@code throw sneakyThrow(t);} so the compiler knows control flow does not continue.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }
}