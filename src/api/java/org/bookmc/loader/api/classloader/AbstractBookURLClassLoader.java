package org.bookmc.loader.api.classloader;

import org.bookmc.loader.api.classloader.transformers.BookTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBookURLClassLoader extends URLClassLoader {
    protected final List<String> classLoaderExclusions = new ArrayList<>();
    protected final List<String> transformationExclusions = new ArrayList<>();
    protected final List<BookTransformer> transformers = new ArrayList<>();

    private final boolean transformable;

    private final Map<String, byte[]> classCache = new HashMap<>();

    public AbstractBookURLClassLoader(URL[] urls, ClassLoader parent, boolean transformable) {
        super(urls, parent);
        this.transformable = transformable;

        // JDK internals, we don't like this stuff!
        addClassLoaderExclusion("sun.");
        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("javax.");
        addClassLoaderExclusion("com.sun.");

        // JDK internals, we hate this stuff also
        addClassLoaderExclusion("jdk.internal.");
        addClassLoaderExclusion("jdk.jfr.");

        addClassLoaderExclusion("org.bookmc.loader.");

        addClassLoaderExclusion("org.apache.logging.");
        addClassLoaderExclusion("org.lwjgl.");
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                } catch (ClassNotFoundException ignored) {

                }

                if (c == null) {
                    c = getParent().loadClass(name);
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (String exclusion : classLoaderExclusions) {
            if (name.startsWith(exclusion)) {
                return getParent().loadClass(name);
            }
        }

        byte[] clazz = getClassAsBytes(name);

        if (transformable) {
            if (!transformationExclusions.contains(name)) {
                clazz = transformClass(name, clazz);
            }
        }

        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }

        // TODO: Implement CodeSigner
        return defineClass(name, clazz, 0, clazz.length, new CodeSource(getClass(name), new CodeSigner[0]));
    }

    public byte[] transformClass(String name, byte[] clazz) {
        for (BookTransformer transformer : transformers) {
            clazz = transformer.transform(name, clazz);
        }
        return clazz;
    }


    public InputStream getClassAsInputStream(String name) {
        return getResourceAsStream(name.replace(".", "/").concat(".class"));
    }

    public URL getClass(String name) {
        return getResource(name.replace(".", "/").concat(".class"));
    }

    public byte[] getClassAsBytes(String name) {
        if (classCache.containsKey(name)) {
            return classCache.get(name);
        }

        try (InputStream stream = getClassAsInputStream(name)) {
            if (stream != null) {
                byte[] clazz = stream.readAllBytes();
                classCache.put(name, clazz);
                return clazz;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isClassLoaded(String name) {
        synchronized (getClassLoadingLock(name)) {
            return findLoadedClass(name) != null;
        }
    }

    @Override
    public URL getResource(String name) {
        URL res = super.getResource(name);
        return res == null ? getParent().getResource(name) : res;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addClassLoaderExclusion(String toExclude) {
        classLoaderExclusions.add(toExclude);
    }

    public void addTransformationExclusion(String toExclude) {
        transformationExclusions.add(toExclude);
    }

    public void registerTransformer(BookTransformer transformer) {
        transformers.add(transformer);
    }
}
