package org.bookmc.loader.api.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class ModClassLoader extends URLClassLoader {
    public ModClassLoader(URL[] urls) {
        super(urls, ModClassLoader.class.getClassLoader());
    }
}
