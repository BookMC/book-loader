package org.bookmc.test.runner.classloader;

import org.bookmc.loader.api.classloader.AppendableURLClassLoader;
import org.bookmc.loader.impl.loader.resolver.ClasspathModResolver;

public class JUnit4ClassLoader extends AppendableURLClassLoader {
    public JUnit4ClassLoader() {
        super(ClasspathModResolver.getSystemClassPathURLs(), null);

        // JDK internals, we don't like this stuff!
        addClassLoaderExclusion("sun.");
        addClassLoaderExclusion("java.");
        addClassLoaderExclusion("javax.");
        addClassLoaderExclusion("com.sun.");

        // JDK internals, we hate this stuff also
        addClassLoaderExclusion("jdk.internal.");
        addClassLoaderExclusion("jdk.jfr.");
        addClassLoaderExclusion("org.bookmc.test.");
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (String exclusion : classLoaderExclusions) {
            if (name.startsWith(exclusion)) {
                return JUnit4ClassLoader.class.getClassLoader().loadClass(name);
            }
        }
        return findClass(name);
    }

    @Override
    protected Class<?> findClass(String name) {
        byte[] bytes = getClassAsBytes(name);
        return defineClass(name, bytes, 0, bytes.length);
    }
}
