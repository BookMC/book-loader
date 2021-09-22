package org.bookmc.loader.impl.loader.classloader;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;

import java.net.URL;

public class ModClassLoader extends AbstractBookURLClassLoader {
    public ModClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    /**
     * What's the difference between this and the default implementation!! Well you see the difference
     * is that we don't ask the parent classloader. If what you're looking for isn't on this classlodaer
     * you haven't depended on what you want.
     *
     * @param name    The name of the class to be loaded
     * @param resolve Whether the class should be "resolved" {@link ClassLoader#resolveClass(Class)}
     * @return The loaded class object
     * @throws ClassNotFoundException Failed to find the requested class on the classpath
     */
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
                    if (!name.startsWith("net.minecraft.")) {
                        throw new IllegalStateException(name);
                    } else {
                        c = getParent().loadClass(name);
                    }
                }
            }

            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}
