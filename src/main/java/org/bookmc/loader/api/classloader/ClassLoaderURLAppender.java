package org.bookmc.loader.api.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A custom class used by the candidate system to abstracting appending a URL to a classloader
 * This is for preparation of when we move away from appending all URLs to the LaunchClassLoader
 * and use seperate classloaders per mod, unless they depend on each other / the suggested mod is present.
 *
 * @author ChachyDev 0.3.0
 */
public class ClassLoaderURLAppender {
    private final URLClassLoader classLoader;

    private Method addURL;

    public ClassLoaderURLAppender(URLClassLoader classLoader) {
        if (classLoader.getClass().getName().startsWith("jdk.internal.loader.ClassLoaders$")) {
            throw new IllegalStateException("To avoid issues with later JREs we simple do not allow use of addURL for the AppClassLoader. Rethink your logic");
        }

        this.classLoader = classLoader;

        try {
            this.addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            this.addURL.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simply calls the {@link URLClassLoader#addURL(URL)} via reflection.
     * This is due to it's default visibility being protected so we change
     * the visibility and then invoke it.
     *
     * @param url The URL to be added to the classloader
     * @author ChachyDev
     * @since 0.3.0
     */
    public void add(URL url) {
        try {
            addURL.invoke(classLoader, url);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }
}
