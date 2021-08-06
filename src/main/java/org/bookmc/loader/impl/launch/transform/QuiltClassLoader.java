package org.bookmc.loader.impl.launch.transform;

import org.bookmc.loader.api.classloader.IQuiltClassLoader;
import org.bookmc.loader.api.launch.transform.QuiltRemapper;
import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.bookmc.loader.impl.launch.Launcher;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class QuiltClassLoader extends URLClassLoader implements IQuiltClassLoader {
    private final Map<String, byte[]> classCache = new HashMap<>();
    private final Map<String, QuiltTransformer> transformers = new HashMap<>();
    private final Map<String, QuiltRemapper> quiltRemappers = new HashMap<>();
    private final List<String> exclusions = new ArrayList<>();

    public QuiltClassLoader() {
        super(new URL[0], QuiltClassLoader.class.getClassLoader());

        addClassLoaderExclusion("sun.");
        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("javax.");
        addClassLoaderExclusion("org.xml.");
        addClassLoaderExclusion("org.w3c.");
        addClassLoaderExclusion("org.lwjgl.");
        addClassLoaderExclusion("org.objectweb.");
        addClassLoaderExclusion("org.apache.logging.");
        addClassLoaderExclusion("org.bookmc.loader.");

        for (String exclusion : System.getProperty("quilt.classloader.exclusions", "").split(",")) {
            addClassLoaderExclusion(exclusion);
        }
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
                return super.loadClass(name, resolve);
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (String exclusion : exclusions) {
            if (name.startsWith(exclusion)) {
                return getParent().loadClass(name);
            }
        }

        if (isClassLoaded(name)) {
            return getLoadedClass(name);
        }
        byte[] bytes = getClassBytes(name, true);

        if (bytes == null) {
            // Last resort, this also throws an exception if it can't find it
            return Launcher.loadClass(name, false);
        }

        return defineClass(name, bytes, 0, bytes.length);
    }

    @Override
    public URL getResource(String name) {
        ClassLoader parent = getParent();
        URL url = super.getResource(name);
        return url != null ? url : parent.getResource(name);
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
    public URLClassLoader getClassLoader() {
        return this;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public boolean isClassLoaded(String name) {
        synchronized (getClassLoadingLock(name)) {
            return findLoadedClass(name) != null;
        }
    }

    public Class<?> getLoadedClass(String name) {
        synchronized (getClassLoadingLock(name)) {
            return findLoadedClass(name);
        }
    }

    public void registerTransformer(Class<? extends QuiltTransformer> clazz) {
        try {
            String name = clazz.getName();
            QuiltTransformer transformer = (QuiltTransformer) loadClass(name).newInstance();
            if (!transformers.containsKey(name)) {
                transformers.put(name, transformer);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void registerRemapper(Class<? extends QuiltRemapper> clazz) {
        try {
            String name = clazz.getName();
            QuiltRemapper remapper = (QuiltRemapper) loadClass(name).newInstance();
            if (!quiltRemappers.containsKey(name)) {
                quiltRemappers.put(name, remapper);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void addClassLoaderExclusion(String exclusion) {
        exclusions.add(exclusion);
    }

    public List<QuiltTransformer> getTransformers() {
        return Collections.unmodifiableList(new ArrayList<>(transformers.values()));
    }

    public List<String> getExclusions() {
        return Collections.unmodifiableList(exclusions);
    }

    public List<QuiltRemapper> getRemappers() {
        return Collections.unmodifiableList(new ArrayList<>(quiltRemappers.values()));
    }
}
