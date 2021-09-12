package org.bookmc.loader.impl.classloader;

import org.bookmc.loader.api.classloader.AbstractBookClassLoader;
import org.bookmc.loader.api.transformer.QuiltRemapper;
import org.bookmc.loader.api.transformer.QuiltTransformer;
import org.bookmc.loader.impl.launch.BookLauncher;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuiltClassLoader extends AbstractBookClassLoader {
    private final List<QuiltTransformer> transformers = new ArrayList<>();
    private final List<QuiltRemapper> quiltRemappers = new ArrayList<>();

    public QuiltClassLoader() {
        super(new URL[0], QuiltClassLoader.class.getClassLoader());

        // JDK internals, we don't like this stuff!
        addClassLoaderExclusion("sun.");
        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("javax.");
        addClassLoaderExclusion("com.sun.");

        // JDK internals, we hate this stuff also
        addClassLoaderExclusion("jdk.internal.");
        addClassLoaderExclusion("jdk.jfr.");

        // Exclude ourselves and things we use
        addClassLoaderExclusion("org.bookmc.loader.");
        addClassLoaderExclusion("org.apache.logging.");
        addClassLoaderExclusion("org.lwjgl.");
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> initialClass;
        initialClass = checkExclusion(exclusions, name);
        if (initialClass != null) {
            return initialClass;
        }

        if (isClassLoaded(name)) {
            return getLoadedClass(name);
        }

        try {
            CodeSource source = getCodeSource(name);

            byte[] bytes = getClassBytes(name, !exclusions.contains(name));

            if (bytes == null) {
                // Last resort, this also throws an exception if it can't find it
                return BookLauncher.loadClass(name, false);
            }

            return defineClass(name, bytes, 0, bytes.length, source);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    @Override
    public URL getResource(String name) {
        ClassLoader parent = getParent();
        URL url = super.getResource(name);
        return url != null ? url : parent.getResource(name);
    }

    @Override
    public boolean isClassLocatable(String name) {
        return getResourceAsStream(name.replace(".", "/").concat(".class")) != null;
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

    public void registerTransformer(QuiltTransformer transformer) {
        try {
            transformers.add(transformer);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void registerRemapper(QuiltRemapper remapper) {
        try {
            quiltRemappers.add(remapper);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void addClassLoaderExclusion(String exclusion) {
        exclusions.add(exclusion);
    }

    public List<QuiltTransformer> getTransformers() {
        return Collections.unmodifiableList(transformers);
    }

    public List<String> getExclusions() {
        return Collections.unmodifiableList(exclusions);
    }

    public List<QuiltRemapper> getRemappers() {
        return Collections.unmodifiableList(quiltRemappers);
    }
}
