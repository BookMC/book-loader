package org.bookmc.loader.utils;

import java.util.HashMap;
import java.util.Map;

public class ClassUtils {
    private static final Map<String, Boolean> availableMap = new HashMap<>();

    public static boolean isClassAvailable(String name) {
        if (availableMap.containsKey(name)) {
            return availableMap.get(name);
        }

        try {
            Class.forName(name);
            availableMap.put(name, true);
            return true;
        } catch (ClassNotFoundException e) {
            availableMap.put(name, false);
            return false;
        }
    }
}
