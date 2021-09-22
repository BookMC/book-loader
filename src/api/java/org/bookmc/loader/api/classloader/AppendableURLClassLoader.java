package org.bookmc.loader.api.classloader;

import java.net.URL;

public abstract class AppendableURLClassLoader extends ClassExcludableURLClassLoader {
    public AppendableURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
