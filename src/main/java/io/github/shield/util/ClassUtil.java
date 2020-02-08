package io.github.shield.util;

public class ClassUtil {

    public static Object[] combine(Object arg1, Object[] rest) {
        Object[] result = new Object[rest.length + 1];
        result[0] = arg1;
        System.arraycopy(rest,0,result,1,rest.length);
        return result;
    }

    public static Class[] toClassArray(Object[] args) {
        Class[] classArray = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            Class clazz = args[i].getClass();
            String className = clazz.getName();
            if (className.contains("EnhancerBy")) {
                classArray[i] = args[i].getClass().getSuperclass();
            } else {
                classArray[i] = args[i].getClass();
            }
        }
        return classArray;
    }
}
