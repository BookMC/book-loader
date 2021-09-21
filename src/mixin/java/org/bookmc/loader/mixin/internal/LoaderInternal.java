package org.bookmc.loader.mixin.internal;

import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModContainer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoaderInternal {
    public static boolean isClassLoaded(String name) {
        boolean loaded = BookLoaderBase.INSTANCE.getGlobalClassLoader().isClassLoaded(name);

        if (!loaded) {
            for (ModContainer container : BookLoaderBase.INSTANCE.getContainers().values()) {
                if (container.getClassLoader() != BookLoaderBase.INSTANCE.getGlobalClassLoader()) {
                    if (container.getClassLoader().isClassLoaded(name)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        if (BookLoaderBase.INSTANCE.getGlobalClassLoader().getClassAsBytes(name) != null) {
            return Class.forName(name, initialize, BookLoaderBase.INSTANCE.getGlobalClassLoader());
        }

        for (ModContainer container : BookLoaderBase.INSTANCE.getContainers().values()) {
            if (container.getClassLoader() != BookLoaderBase.INSTANCE.getGlobalClassLoader()) {
                if (container.getClassLoader().getClassAsBytes(name) != null) {
                    return Class.forName(name, initialize, container.getClassLoader());
                }
            }
        }

        throw new ClassNotFoundException(name);
    }

    public static URL[] getURLs() {
        List<URL> urlList = new ArrayList<>(Arrays.asList(BookLoaderBase.INSTANCE.getGlobalClassLoader().getURLs()));
        for (ModContainer container : BookLoaderBase.INSTANCE.getContainers().values()) {
            if (container.getClassLoader() != BookLoaderBase.INSTANCE.getGlobalClassLoader()) {
                urlList.addAll(Arrays.asList(container.getClassLoader().getURLs()));
            }
        }

        return urlList.toArray(new URL[0]);
    }

    public static ClassNode getClassNode(String name, boolean runTransformers) {
        byte[] bytes = BookLoaderBase.INSTANCE.getGlobalClassLoader().getClassAsBytes(name);
        if (bytes == null) {
            for (ModContainer container : BookLoaderBase.INSTANCE.getContainers().values()) {
                if (container.getClassLoader() != BookLoaderBase.INSTANCE.getGlobalClassLoader()) {
                    byte[] containerResolvedBytes = container.getClassLoader().getClassAsBytes(name);
                    if (containerResolvedBytes != null) {
                        bytes = containerResolvedBytes;
                        break;
                    }
                }
            }
            if (bytes == null) {
                return null;
            }
        }

        if (runTransformers) {
            bytes = BookLoaderBase.INSTANCE.getGlobalClassLoader().transformClass(name, bytes);
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        return classNode;
    }

    public static InputStream getResourceAsStream(String name) {
        InputStream stream = BookLoaderBase.INSTANCE.getGlobalClassLoader().getResourceAsStream(name);
        if (stream != null) {
            return stream;
        }

        for (ModContainer container : BookLoaderBase.INSTANCE.getContainers().values()) {
            if (container.getClassLoader() != BookLoaderBase.INSTANCE.getGlobalClassLoader()) {
                InputStream stream1 = container.getClassLoader().getResourceAsStream(name);
                if (stream1 != null) {
                    return stream1;
                }
            }
        }

        return null;
    }
}
