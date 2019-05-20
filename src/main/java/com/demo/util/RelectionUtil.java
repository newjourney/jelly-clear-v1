package com.demo.util;

public class RelectionUtil extends SecurityManager {

    static final RelectionUtil relection = new RelectionUtil();

    private RelectionUtil() {
    }

    @Override
    protected Class<?>[] getClassContext() {
        return super.getClassContext();
    }

    public static Class<?> getCallerClass() {
        return getCallerClass(2);
    }

    /**
     * @param depth 0 is the @getCallerClass`s caller
     * @return
     */
    public static Class<?> getCallerClass(int depth) {
        Class<?>[] classes = relection.getClassContext();
        int index = depth + 2;
        int len = classes.length;
        return len > index ? classes[index] : classes[len - 1];
    }

    public static Class<?> getCallerClass(final Class<?> anchor) {
        boolean next = false;
        for (final Class<?> clazz : relection.getClassContext()) {
            if (anchor.equals(clazz)) {
                next = true;
                continue;
            }
            if (next)
                return clazz;
        }
        return Object.class;
    }

}
