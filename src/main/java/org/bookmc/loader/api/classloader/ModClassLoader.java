package org.bookmc.loader.api.classloader;

import org.bookmc.loader.impl.launch.Launcher;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModClassLoader extends URLClassLoader implements IQuiltClassLoader {
    private final Map<String, byte[]> classCache = new HashMap<>();
    private final List<String> exclusions = new ArrayList<>();
    // Bit confusing but excludes exclusions (net.minecraft. is a big wildcard and some people still want that on their modclassloader)
    private final List<String> exclusionExclusions = new ArrayList<>();

    public ModClassLoader(URL[] urls) {
        super(urls, Launcher.getQuiltClassLoader());

        addClassLoaderExclusion("net.minecraft."); // We don't want minecraft on our classloader!
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);

            if (c != null) {
                return c;
            }

            try {
                return findClass(name);
            } catch (ClassNotFoundException ignored) {
                throw new ClassNotFoundException(name);
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (String exclusion : Launcher.getQuiltClassLoader().getExclusions()) {
            if (name.startsWith(exclusion)) {
                return getParent().loadClass(name);
            }
        }

        for (String exclusion : exclusions) {
            if (name.startsWith(exclusion) && !exclusionExclusions.contains(exclusion)) {
                return getParent().loadClass(name);
            }
        }

        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = findLoadedClass(name);
            if (clazz != null) {
                return clazz;
            }
        }

        byte[] bytes = getClassBytes(name, true);

        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }

        return defineClass(name, bytes, 0, bytes.length);
    }

    @Override
    public byte[] getCachedClass(String name) {
        return classCache.get(name);
    }

    @Override
    public void putCachedClass(String name, byte[] bytes) {
        classCache.put(name, bytes);
    }

    @Override
    public boolean isClassLoaded(String name) {
        synchronized (getClassLoadingLock(name)) {
            return findLoadedClass(name) != null;
        }
    }

    @Override
    public URLClassLoader getClassLoader() {
        return this;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public boolean isClassAvailable(String name) {
        return getResourceAsStream(name.replace(".", "/").concat(".class")) != null;
    }

    public void addClassLoaderExclusion(String toExclude) {
        exclusions.add(toExclude);
    }

    public void addClassLoaderExclusionExclusion(String toExclude) {
        exclusionExclusions.add(toExclude);
    }
}
