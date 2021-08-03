package org.bookmc.loader.api.classloader;

import java.net.URLClassLoader;

public interface IQuiltClassLoader {
    byte[] getClassBytes(String name, boolean transform);

    URLClassLoader getClassLoader();
}
