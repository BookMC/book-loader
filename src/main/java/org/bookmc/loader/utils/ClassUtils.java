package org.bookmc.loader.utils;

import net.minecraft.launchwrapper.LaunchClassLoader;

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
        return LaunchClassLoader.getSystemResourceAsStream(resource) != null;
    }
}
