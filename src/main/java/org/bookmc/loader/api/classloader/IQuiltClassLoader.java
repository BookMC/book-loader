package org.bookmc.loader.api.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public interface IQuiltClassLoader {
    byte[] getClassBytes(String name, boolean transform);

    URLClassLoader getClassLoader();

    void addURL(URL url);
}
