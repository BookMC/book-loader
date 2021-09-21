package org.bookmc.loader.impl.launch;

import java.lang.reflect.InvocationTargetException;

/**
 * Present to keep compatibility with outdated workspaces
 */
public class QuiltServer {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = Class.forName("org.bookmc.loader.impl.launch.server.BookServer");
        clazz.getDeclaredMethod("main", String[].class)
            .invoke(null, (Object) args);
    }
}
