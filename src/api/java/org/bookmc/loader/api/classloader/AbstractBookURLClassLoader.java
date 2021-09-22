package org.bookmc.loader.api.classloader;

import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBookURLClassLoader extends AppendableURLClassLoader {
    private final Map<String, byte[]> classCache = new HashMap<>();

    public AbstractBookURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
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

        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }

        // TODO: Implement CodeSigner
        return defineClass(name, clazz, 0, clazz.length, new CodeSource(getClass(name), new CodeSigner[0]));
    }


    @Override
    public URL getResource(String name) {
        URL res = super.getResource(name);
        return res == null ? getParent().getResource(name) : res;
    }

    private static class TransformableClassLoader$0 extends TransformableURLClassLoader {
        public TransformableClassLoader$0(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
    }
}
