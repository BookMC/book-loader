package org.bookmc.loader.api.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBookURLClassLoader extends URLClassLoader {
    private final List<String> classLoaderExclusions = new ArrayList<>();
    private final List<String> transformationExclusions = new ArrayList<>();

    public AbstractBookURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
