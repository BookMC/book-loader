package org.bookmc.loader.utils;

public class ClassUtils {
    public static boolean isClassAvailable(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isResourceAvailable(String resource) {
        return ClassUtils.class.getResourceAsStream(resource) != null;
    }
}
