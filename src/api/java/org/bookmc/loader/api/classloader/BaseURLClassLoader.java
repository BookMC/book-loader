package org.bookmc.loader.api.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseURLClassLoader extends URLClassLoader {
    protected Map<String, byte[]> classCache = new HashMap<>();

    public BaseURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public InputStream getClassAsInputStream(String name) {
        return getResourceAsStream(name.replace(".", "/").concat(".class"));
    }

    public byte[] modifyResolvedBytes(String name, byte[] bytes) {
        return bytes;
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
}
