package org.bookmc.loader.api.classloader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class ClassExcludableURLClassLoader extends BaseURLClassLoader {
    protected List<String> classLoaderExclusions = new ArrayList<>();

    public ClassExcludableURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public List<String> getClassLoaderExclusions() {
        return classLoaderExclusions;
    }

    public void addClassLoaderExclusion(String toExclude) {
        classLoaderExclusions.add(toExclude);
    }
}
